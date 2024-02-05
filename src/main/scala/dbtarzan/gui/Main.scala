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


import org.apache.pekko.actor.{ActorRef, ActorSystem, Props}
import scalafx.application.JFXApp3

/** Main class, starts the main gui, the actors, and connects them together */
object Main extends JFXApp3 {
  override def start(): Unit = {
    println(s"Named commend line arguments: ${parameters.named.mkString(",")}")
    val version = versionFromManifest()
    val configPaths = extractConfigPaths()
    val connectionDatas = readConnectionDatas(configPaths.connectionsConfigPath)
    val globalData = GlobalDataReader.read(configPaths.globalConfigPath)
    val composites = readComposites(configPaths.compositeConfigPath)
    val localization = Localizations.of(globalData.language)
    val system = ActorSystem("Sys")
    val guiActor: ActorRef = system.actorOf(Props(
      new GUIActor(configPaths, connectionDatas, composites, globalData, localization, version)
    ).withDispatcher("my-pinned-dispatcher"), "guiWorker")
    val connectionsActor: ActorRef = system.actorOf(Props(
      new ConnectionsActor(connectionDatas, composites,localization, configPaths.keyFilesDirPath)
    ).withDispatcher("my-pinned-dispatcher"), "configWorker")
    val logActor: ActorRef = system.actorOf(Props(
      new LogActor(configPaths.logFilePath)
    ).withDispatcher("my-pinned-dispatcher"), "logWorker")
    guiActor ! GUIInitData(connectionsActor, logActor)
    connectionsActor ! ConnectionsInitData(guiActor, logActor)
    logActor ! LogInitData(guiActor)

    val log = new Logger(guiActor)
  }

  private def extractConfigPaths() : ConfigPath = {
    val configsPath = parameters.named.getOrElse("configPath", Option(System.getProperty("configPath")).getOrElse("."))
    val globalConfigPath = Paths.get(configsPath, "global.config")
    val connectionsConfigPath = Paths.get(configsPath, "connections.config")
    val compositesConfigPath = Paths.get(configsPath, "composites.config")
    val logFilePath = Paths.get(configsPath, "dbtarzan.log")
    println(s"Current directory: ${System.getProperty("user.dir")} globalConfigPath:${globalConfigPath} connectionsConfigPath:${connectionsConfigPath}  compositesConfigPath:${compositesConfigPath}")
    ConfigPath(globalConfigPath, connectionsConfigPath, Paths.get(configsPath), compositesConfigPath, logFilePath)
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
}

