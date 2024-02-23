package dbtarzan.gui.actor

import dbtarzan.config.actor.ConnectionsActor
import dbtarzan.config.password.VerificationKey
import dbtarzan.gui.{AppStopper, MainGUI}
import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.gui.interfaces.{TDatabaseList, TDatabases, TGlobal, TLogs}
import dbtarzan.messages.*
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.config.global.GlobalData
import dbtarzan.types.ConfigPath
import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.db.Composite
import scalafx.application.Platform

case class GUIInitData(connectionsActor: ActorRef, logActor: ActorRef)

/* Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf */
class GUIActor(
                configPaths: ConfigPath,
                connectionDatas: List[ConnectionData],
                composites: List[Composite],
                globalData: GlobalData,
                localization: Localization,
                version: String
              ) extends Actor {
  case class GUIInitState(connectionsActor: ActorRef, log: Logger, mainGUI: MainGUI)

  var state: Option[GUIInitState] = None

  def runLater[R](op: MainGUI => R): Unit = {
    Platform.runLater {
      state.foreach(s =>
        try { op(s.mainGUI) } catch { case e : Exception => s.log.error("UI", e) }
      )
    }
  }

  def intiialized: Receive = {
        case rsp: TWithQueryId => runLater{ _.databaseTabs.handleQueryIdMessage(rsp) }
        case rsp: TWithDatabaseId => runLater{ _.databaseTabs.handleDatabaseIdMessage(rsp) }
        case rsp: TWithTableId => runLater { _.databaseTabs.handleTableIdMessage(rsp) }
        case rsp: ResponseTestConnection => runLater { _.global.handleTestConnectionResponse(rsp) }
        case rsp: ResponseSchemaExtraction => runLater { _.global.handleSchemaExtractionResponse(rsp) }
        case msg: TLogMessageGUI => runLater { _.logList.addLogMessage(msg) }
        case msg: DatabaseInfos => runLater { _.databaseList.setDatabaseInfos(msg) }
        case err: ErrorDatabaseAlreadyOpen => runLater { mainGUI =>
          mainGUI.databaseTabs.showDatabase(err.databaseId)
          state.foreach(_.log.warning(localization.databaseAlreadyOpen(DatabaseIdUtil.databaseIdText(err.databaseId))))
        }
  }

  private def initGUI(connectionsActor: ActorRef, log: Logger, stopper: AppStopper) : MainGUI = {
    val mainGUI = new MainGUI(self, connectionsActor, configPaths, localization, globalData.encryptionData.map(_.verificationKey), log, version)
    val connectionDataMap = new ConnectionsDataMap(connectionDatas)
    mainGUI.databaseList.setDatabaseInfos(DatabaseInfos(
      DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionDatas) ++
        DatabaseInfoFromConfig.extractCompositeInfos(composites, connectionDataMap.connectionDataFor)
    ))
    mainGUI.onDatabaseSelected({ case (databaseInfo, encryptionKey, loginPasswords) => {
      log.info(localization.openingDatabase(DatabaseIdUtil.databaseInfoText(databaseInfo)))
      connectionsActor ! QueryDatabase(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords)
    }
    })
    mainGUI.onForeignKeyToFile({
      case (databaseInfo, encryptionKey, loginPasswords) => connectionsActor ! CopyToFile(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords)
    })
    mainGUI.onCloseApp(
      () => stopper.closeApp(() => {
        scalafx.application.Platform.exit()
        System.exit(0)
      })
    )
    mainGUI
  }

  def receive: PartialFunction[Any,Unit] = {
    case initData : GUIInitData  => {
      val log = new Logger(initData.logActor)
      val stopper = new AppStopper(context.system, context.self, initData.connectionsActor, initData.logActor)
      Platform.runLater {
        val mainGUI = initGUI(initData.connectionsActor, log, stopper)
        state = Some(GUIInitState(initData.connectionsActor, log, mainGUI))
        context.become(intiialized)
      }
    }
  }
}