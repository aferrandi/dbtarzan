package dbtarzan.gui

import scalafx.application.JFXApp
import java.nio.file.{ Paths, Path }

import dbtarzan.config.connections.ConnectionDataReader
import dbtarzan.config.global.GlobalDataReader
import dbtarzan.gui.actor.GUIWorker
import dbtarzan.config.actor.ConnectionsWorker
import dbtarzan.types.ConfigPath
import dbtarzan.messages.{QueryDatabase, CopyToFile, DatabaseIds, ConnectionDatas, Logger }
import dbtarzan.db.DatabaseId
import dbtarzan.localization.Localizations

/** Main class, starts the main gui, the actors, and connects them together */
object Main extends JFXApp {
  println("Named commend line arguments:"+ parameters.named.mkString(","))
  val version = versionFromManifest()
  val configPaths = extractConnectionsConfigPath()
  val connectionDatas = readConnectionDatas(configPaths.connectionsConfigPath)
  val globalData = GlobalDataReader.read(configPaths.globalConfigPath)
  val localization = Localizations.of(globalData.language)
  val mainGUI = new MainGUI(configPaths, localization, version, openWeb, closeApp)
  val actors = new ActorHandler(
    () => new GUIWorker(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList), 
    guiActor => new ConnectionsWorker(connectionDatas, guiActor, localization)
    ) 
  mainGUI.setActors(actors.guiActor, actors.connectionsActor)
  val log = new Logger(actors.guiActor)
  mainGUI.databaseList.setDatabaseIds(databaseIds(connectionDatas))
  mainGUI.onDatabaseSelected( { case databaseId => {
    log.info(localization.openingDatabase(databaseId.databaseName))
    actors.connectionsActor ! QueryDatabase(databaseId) 
    }})
  mainGUI.onForeignKeyToFile( { case databaseId => actors.connectionsActor ! CopyToFile(databaseId) })

  private def databaseIds(connections : ConnectionDatas)  =
    DatabaseIds(connections.datas.map(c => DatabaseId(c.name)))

  private def extractConnectionsConfigPath() : ConfigPath = {
    var configsPath = parameters.named.getOrElse("configPath", Option(System.getProperty("configPath")).getOrElse(".") )
    val globalConfigPath = Paths.get(configsPath, "global.config")
    val connectionsConfigPath = Paths.get(configsPath, "connections.config")
    println("Current directory: "+System.getProperty("user.dir")
      +" globalConfigPath:"+globalConfigPath
      +" connectionsConfigPath:"+connectionsConfigPath
      )
    ConfigPath(globalConfigPath, connectionsConfigPath)
  }

  private def readConnectionDatas(connectionsConfigPath: Path) : ConnectionDatas = {
    val connections = ConnectionDataReader.read(connectionsConfigPath)
    ConnectionDatas(connections.sortBy(_.name))    
  }

  def closeApp() : Unit = actors.closeApp(() => scalafx.application.Platform.exit())

  private def openWeb(url : String) : Unit = try
    {  hostServices.showDocument(url) }
    catch {
      case e: Throwable =>  new ProcessBuilder("x-www-browser", url).start()
    }
  
  private def versionFromManifest() = Option(getClass().getPackage().getImplementationVersion()).getOrElse("")
}

