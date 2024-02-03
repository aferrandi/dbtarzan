package dbtarzan.gui

import java.nio.file.{Path, Paths}
import dbtarzan.config.actor.{ConnectionsActor, ConnectionsInitData}
import dbtarzan.config.composite.CompositeReader
import dbtarzan.config.connections.{ConnectionData, ConnectionDataReader, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.config.global.GlobalDataReader
import dbtarzan.db.{Composite, DatabaseId, SimpleDatabaseId}
import dbtarzan.gui.actor.{GUIActor, GUIInitData}
import dbtarzan.localization.Localizations
import dbtarzan.log.actor.{LogActor, LogInitData, Logger}
import dbtarzan.messages.*
import dbtarzan.types.ConfigPath

import scala.language.postfixOps
import org.apache.pekko.actor.{ActorRef, ActorSystem, Props}
import scalafx.application.JFXApp3

/** Main class, starts the main gui, the actors, and connects them together */
object Main extends JFXApp3 {
  override def start(): Unit = {
    println(s"Named commend line arguments: ${parameters.named.mkString(",")}")
    val version = versionFromManifest()
    val configPaths = extractConnectionsConfigPath()
    val connectionDatas = readConnectionDatas(configPaths.connectionsConfigPath)
    val globalData = GlobalDataReader.read(configPaths.globalConfigPath)
    val composites = readComposites(configPaths.compositeConfigPath)
    val localization = Localizations.of(globalData.language)
    val mainGUI = new MainGUI(configPaths, localization, globalData.encryptionData.map(_.verificationKey), version)
    val system = ActorSystem("Sys")
    val guiActor: ActorRef = system.actorOf(Props(
      new GUIActor(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList, mainGUI.global, localization)
    ).withDispatcher("my-pinned-dispatcher"), "guiWorker")
    val connectionsActor: ActorRef = system.actorOf(Props(
      new ConnectionsActor(connectionDatas, composites,localization, configPaths.keyFilesDirPath)
    ).withDispatcher("my-pinned-dispatcher"), "configWorker")
    val logActor: ActorRef = system.actorOf(Props(
      new LogActor()
    ).withDispatcher("my-pinned-dispatcher"), "logWorker")
    guiActor ! GUIInitData(logActor)
    connectionsActor ! ConnectionsInitData(guiActor, logActor)
    logActor ! LogInitData(guiActor)

    val log = new Logger(guiActor)
    mainGUI.postInit(guiActor, connectionsActor, log)
    val connectionDataMap = new ConnectionsDataMap(connectionDatas)
    mainGUI.databaseList.setDatabaseInfos(DatabaseInfos(
      DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionDatas) ++ 
        DatabaseInfoFromConfig.extractCompositeInfos(composites, connectionDataMap.connectionDataFor)
    ))
    mainGUI.onDatabaseSelected({ case (databaseInfo, encryptionKey, loginPasswords) => {
      log.info(localization.openingDatabase(DatabaseIdUtil.databaseInfoText(databaseInfo)))
      connectionsActor ! QueryDatabase(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords )
    }})
    mainGUI.onForeignKeyToFile({
      case (databaseInfo, encryptionKey, loginPasswords) => connectionsActor ! CopyToFile(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords)
    })
    mainGUI.onCloseApp(
      () => closeApp(system, guiActor, connectionsActor, logActor, () => {
        scalafx.application.Platform.exit()
        System.exit(0)
      })
    )
  }

  private def extractConnectionsConfigPath() : ConfigPath = {
    val configsPath = parameters.named.getOrElse("configPath", Option(System.getProperty("configPath")).getOrElse("."))
    val globalConfigPath = Paths.get(configsPath, "global.config")
    val connectionsConfigPath = Paths.get(configsPath, "connections.config")
    val compositesConfigPath = Paths.get(configsPath, "composites.config")
    println("Current directory: "+System.getProperty("user.dir")
      +" globalConfigPath:"+globalConfigPath
      +" connectionsConfigPath:"+connectionsConfigPath
      +" compositesConfigPath:"+compositesConfigPath
      )
    ConfigPath(globalConfigPath, connectionsConfigPath, Paths.get(configsPath), compositesConfigPath)
  }

  private def readConnectionDatas(connectionsConfigPath: Path) : List[ConnectionData] = {
    val connections = ConnectionDataReader.read(connectionsConfigPath)
    connections.sortBy(_.name)
  }

  private def readComposites(compositeConfigPath: Path): List[Composite] = {
    val composites = CompositeReader.read(compositeConfigPath)
    composites.sortBy(_.compositeId.compositeName)
  }


  private def versionFromManifest() = Option(getClass.getPackage.getImplementationVersion).getOrElse("")

  def closeApp(system: ActorSystem, guiActor: ActorRef, connectionsActor: ActorRef, logActor: ActorRef, onExit: Runnable): Unit = {
    println("application exit")
    import org.apache.pekko.pattern.gracefulStop
    import scala.concurrent._
    import scala.concurrent.duration._
    import ExecutionContext.Implicits.global
    val stopAll = for {
      stopGui: Boolean <- gracefulStop(guiActor, 1 seconds)
      stopLog: Boolean <- gracefulStop(logActor, 1 seconds)
      stopConfig: Boolean <- gracefulStop(connectionsActor, 1 seconds)
    } yield stopGui && stopLog && stopConfig
    stopAll.foreach(_ => {
      system.terminate()
      println("shutdown")
      system.registerOnTermination(onExit)
    })
  }
}

