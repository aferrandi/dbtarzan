package dbtarzan.gui

import java.nio.file.{Path, Paths}
import dbtarzan.config.actor.ConnectionsActor
import dbtarzan.config.composite.CompositeReader
import dbtarzan.config.connections.{ConnectionData, ConnectionDataReader, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.config.global.GlobalDataReader
import dbtarzan.db.{Composite, DatabaseId, SimpleDatabaseId}
import dbtarzan.gui.actor.GUIActor
import dbtarzan.localization.Localizations
import dbtarzan.messages.*
import dbtarzan.types.ConfigPath
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
    val actors = new ActorHandler(
      () => new GUIActor(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList, mainGUI.global, localization),
      guiActor => new ConnectionsActor(connectionDatas, composites, guiActor, localization, configPaths.keyFilesDirPath)
    )
    mainGUI.postInit(actors.guiActor, actors.connectionsActor)
    val log = new Logger(actors.guiActor)
    val connectionDataMap = new ConnectionsDataMap(connectionDatas)
    mainGUI.databaseList.setDatabaseInfos(DatabaseInfos(
      DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionDatas) ++ 
        DatabaseInfoFromConfig.extractCompositeInfos(composites, connectionDataMap.connectionDataFor)
    ))
    mainGUI.onDatabaseSelected({ case (databaseInfo, encryptionKey, loginPasswords) => {
      log.info(localization.openingDatabase(DatabaseIdUtil.databaseInfoText(databaseInfo)))
      actors.connectionsActor ! QueryDatabase(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords )
    }})
    mainGUI.onForeignKeyToFile({
      case (databaseInfo, encryptionKey, loginPasswords) => actors.connectionsActor ! CopyToFile(DatabaseIdUtil.databaseIdFromInfo(databaseInfo), encryptionKey, loginPasswords)
    })
    mainGUI.onCloseApp(
      () => actors.closeApp(() => {
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

}

