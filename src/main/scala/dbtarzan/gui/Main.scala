package dbtarzan.gui

import java.nio.file.{Path, Paths}
import dbtarzan.config.actor.ConnectionsActor
import dbtarzan.config.composite.CompositeReader
import dbtarzan.config.connections.ConnectionDataReader
import dbtarzan.config.global.GlobalDataReader
import dbtarzan.db.DatabaseId
import dbtarzan.db.actor.CompositeActor
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
  private val composites = readComposites(configPaths.compositeConfigPath)
  private val localization = Localizations.of(globalData.language)
  val mainGUI = new MainGUI(configPaths, localization, globalData.encryptionData.map(_.verificationKey), version, closeApp)
  val actors = new ActorHandler(
    () => new GUIActor(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList, mainGUI.compositeList, mainGUI.global, localization),
    guiActor => new ConnectionsActor(connectionDatas, guiActor, localization, configPaths.keyFilesDirPath),
      guiActor => new CompositeActor(composites, guiActor, localization)
    ) 
  mainGUI.postInit(actors.guiActor, actors.connectionsActor, actors.compositeActor)
  val log = new Logger(actors.guiActor)
  mainGUI.databaseList.setDatabaseIds(databaseIdsFromConnections(connectionDatas))
  mainGUI.onDatabaseSelected( { case (databaseId, encryptionKey) => {
    log.info(localization.openingDatabase(databaseId.databaseName))
    actors.connectionsActor ! QueryDatabase(databaseId, encryptionKey) 
    }})
  mainGUI.onForeignKeyToFile( { 
    case (databaseId, encryptionKey) => actors.connectionsActor ! CopyToFile(databaseId, encryptionKey) 
    })

  private def databaseIdsFromConnections(connections : ConnectionDatas)  =
    DatabaseIds(connections.datas.map(c => DatabaseId(c.name)))

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

  private def readConnectionDatas(connectionsConfigPath: Path) : ConnectionDatas = {
    val connections = ConnectionDataReader.read(connectionsConfigPath)
    ConnectionDatas(connections.sortBy(_.name))    
  }

  private def readComposites(compositeConfigPath: Path): Composites = {
    val composites = CompositeReader.read(compositeConfigPath)
    Composites(composites.sortBy(_.compositeId.compositeName))
  }

  def closeApp() : Unit = actors.closeApp(() => {
    scalafx.application.Platform.exit()
    System.exit(0)
  })

  private def versionFromManifest() = Option(getClass.getPackage.getImplementationVersion).getOrElse("")
}

