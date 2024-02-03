package dbtarzan.gui.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.gui.interfaces.{TDatabaseList, TDatabases, TGlobal, TLogs}
import dbtarzan.messages.*
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import scalafx.application.Platform

case class GUIInitData(logActor: ActorRef)

/* Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUIf */
class GUIActor(
                 databases : TDatabases,
                 logs : TLogs,
                 dbList : TDatabaseList,
                 main: TGlobal,
                 localization : Localization
              ) extends Actor {
  var log: Option[Logger] = None

  def runLater[R](op: => R): Unit = {
    Platform.runLater {
      try { op } catch { case e : Exception => log.foreach(_.error("UI", e)) }
    }
  }


  def intiialized: Receive = {
        case rsp: TWithQueryId => runLater { databases.handleQueryIdMessage(rsp) }
        case rsp: TWithDatabaseId => runLater { databases.handleDatabaseIdMessage(rsp) }
        case rsp: TWithTableId => runLater { databases.handleTableIdMessage(rsp) }
        case rsp: ResponseTestConnection => runLater { main.handleTestConnectionResponse(rsp) }
        case rsp: ResponseSchemaExtraction => runLater { main.handleSchemaExtractionResponse(rsp) }
        case msg: TLogMessageGUI => runLater { logs.addLogMessage(msg) }
        case msg: DatabaseInfos => runLater { dbList.setDatabaseInfos(msg) }
        case err: ErrorDatabaseAlreadyOpen => runLater {
            databases.showDatabase(err.databaseId)
            log.foreach(_.warning(localization.databaseAlreadyOpen(DatabaseIdUtil.databaseIdText(err.databaseId))))
        }
  }

  def receive: PartialFunction[Any,Unit] = {
    case initData : GUIInitData  => {
      log = Some(new Logger(initData.logActor))
      context.become(intiialized)
    }
  }

}