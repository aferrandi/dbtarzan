package dbtarzan.gui

import java.nio.file.{Path, Paths}

import dbtarzan.config.actor.ConnectionsActor
import dbtarzan.config.connections.ConnectionDataReader
import dbtarzan.config.global.GlobalDataReader
import dbtarzan.db.DatabaseId
import dbtarzan.gui.actor.GUIActor
import dbtarzan.localization.Localizations
import dbtarzan.messages._
import dbtarzan.types.ConfigPath
import scalafx.application.JFXApp

/** Main class, starts the main gui, the actors, and connects them together */
object Main extends JFXApp {
  println("Named commend line arguments:"+ parameters.named.mkString(","))
  private val version = versionFromManifest()
  private val configPaths = extractConnectionsConfigPath()
  private val connectionDatas = readConnectionDatas(configPaths.connectionsConfigPath)
  private val globalData = GlobalDataReader.read(configPaths.globalConfigPath)
  private val localization = Localizations.of(globalData.language)
  val mainGUI = new MainGUI(configPaths, localization, globalData.encryptionData.map(_.verificationKey), version, openWeb, closeApp)
  val actors = new ActorHandler(
    () => new GUIActor(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList, mainGUI.global, localization),
    guiActor => new ConnectionsActor(connectionDatas, guiActor, localization, configPaths.keyFilesDirPath)
    ) 
  mainGUI.setActors(actors.guiActor, actors.connectionsActor)
  val log = new Logger(actors.guiActor)
  mainGUI.databaseList.setDatabaseIds(databaseIds(connectionDatas))
  mainGUI.onDatabaseSelected( { case (databaseId, encryptionKey) => {
    log.info(localization.openingDatabase(databaseId.databaseName))
    actors.connectionsActor ! QueryDatabase(databaseId, encryptionKey) 
    }})
  mainGUI.onForeignKeyToFile( { 
    case (databaseId, encryptionKey) => actors.connectionsActor ! CopyToFile(databaseId, encryptionKey) 
    })

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
    ConfigPath(globalConfigPath, connectionsConfigPath, Paths.get(configsPath))
  }

  private def readConnectionDatas(connectionsConfigPath: Path) : ConnectionDatas = {
    val connections = ConnectionDataReader.read(connectionsConfigPath)
    ConnectionDatas(connections.sortBy(_.name))    
  }

  def closeApp() : Unit = actors.closeApp(() => {
    scalafx.application.Platform.exit()
    System.exit(0)
  })

  private def versionFromManifest() = Option(getClass.getPackage.getImplementationVersion).getOrElse("")

  private def openWeb(url : String) : Unit = openWebTry1(url)

  private def openWebTry1(url: String): Unit = try {
      val p = new ProcessBuilder("xdg-open", url).start()
      if (!p.isAlive)
        openWebTry2(url)
    }
    catch { case e: Throwable => openWebTry2(url) }

  private def openWebTry2(url: String): Unit =
    hostServices.showDocument(url)
}

