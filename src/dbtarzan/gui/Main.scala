package dbtarzan.gui

import scalafx.application.JFXApp
import java.nio.file.Paths

import dbtarzan.config.ConnectionDataReader
import dbtarzan.gui.actor.GUIWorker
import dbtarzan.config.actor.ConnectionsWorker
import dbtarzan.types.ConfigPath
import dbtarzan.messages.{QueryDatabase, CopyToFile, DatabaseIds, ConnectionDatas, Logger }
import dbtarzan.db.DatabaseId

/** Main class, starts the main gui, the actors, and connects them together */
object Main extends JFXApp {
  println("Named commend line arguments:"+ parameters.named.mkString(","))
  val version = versionFromManifest()
  val connectionsConfigPath = extractConnectionsConfigPath()
  val connectionDatas = readConnectionDatas(connectionsConfigPath)
  val mainGUI = new MainGUI(connectionsConfigPath, version, openWeb, closeApp)
  val actors = new ActorHandler(
    () => new GUIWorker(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList), 
    guiActor => new ConnectionsWorker(connectionDatas, guiActor)
    ) 
  mainGUI.setActors(actors.guiActor, actors.connectionsActor)
  val log = new Logger(actors.guiActor)
  mainGUI.databaseList.setDatabaseIds(databaseIds(connectionDatas))
  mainGUI.onDatabaseSelected( { case databaseId => {
    log.info("Opening database "+databaseId.databaseName)
    actors.connectionsActor ! QueryDatabase(databaseId) 
    }})
  mainGUI.onForeignKeyToFile( { case databaseId => actors.connectionsActor ! CopyToFile(databaseId) })

  private def databaseIds(connections : ConnectionDatas)  =
    DatabaseIds(connections.datas.map(c => DatabaseId(c.name)))

  private def extractConnectionsConfigPath() : ConfigPath = {
    var configsPath = parameters.named.getOrElse("configPath", Option(System.getProperty("configPath")).getOrElse(".") )
    val connectionsConfigPath = ConfigPath(Paths.get(configsPath, "connections.config"))
    println("Current directory: "+System.getProperty("user.dir")+" connectionsConfigPath:"+connectionsConfigPath.path)
    connectionsConfigPath
  }

  private def readConnectionDatas(connectionsConfigPath: ConfigPath) : ConnectionDatas = {
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

