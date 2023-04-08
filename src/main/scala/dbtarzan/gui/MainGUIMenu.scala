package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.gui.config.composite.CompositeEditorStarter
import dbtarzan.gui.config.connections.ConnectionEditorStarter
import dbtarzan.gui.config.global.GlobalEditorStarter
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.types.ConfigPath
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.{Menu, MenuBar}



class MainGUIMenu(
                  configPaths: ConfigPath,
                  localization: Localization,
                  encryptionKeyExtractor: EncryptionKeyExtractor,
                  global: Global) {

  case class PostInitData(stage: PrimaryStage, guiActor: ActorRef, connectionsActor: ActorRef)

  private var postInitData: Option[PostInitData] = None

  def postInit(stage: PrimaryStage, guiActor: ActorRef, connectionsActor: ActorRef): Unit = {
    this.postInitData = Some(PostInitData(stage, guiActor, connectionsActor))
  }

  def buildMenu(): MenuBar = new MenuBar {
    menus = List(
      buildSettingsMenu(),
      buildHelpMenu()
    )
  }

  private def buildSettingsMenu() = new Menu(localization.settings) {
    items = List(
      JFXUtil.menuItem(localization.globalSettings, openGlobalEditor),
      JFXUtil.menuItem(localization.editConnections, openConnectionsEditor),
      JFXUtil.menuItem(localization.editComposites, openCompositeEditor)
    )
  }

  private def buildHelpMenu() = new Menu(localization.help) {
    items = List(
      JFXUtil.menuItem(localization.documentation, () => OpenWeb.openWeb("https://aferrandi.github.io/dbtarzan/")),
    )
  }

  private def openConnectionsEditor(): Unit = {
    postInitData match {
      case Some(pa) => {
        new Logger(pa.guiActor).info(localization.editingConnectionFile(configPaths.connectionsConfigPath))
        encryptionKeyExtractor.extractEncryptionKey(pa.stage) match {
          case Some(key) =>
            global.setConnectionEditor(ConnectionEditorStarter.openConnectionsEditor(pa.stage, pa.connectionsActor, configPaths.connectionsConfigPath, key, localization))
          case None => println("MainGUI: encryptionKey not entered")
        }
      }
      case None => println("MainGUI: guiActor not defined")
    }
  }

  private def openCompositeEditor(): Unit = {
    postInitData match {
      case Some(pa) => {
        new Logger(pa.guiActor).info(localization.editingCompositeFile(configPaths.compositeConfigPath))
        CompositeEditorStarter.openCompositeEditor(pa.stage, configPaths.compositeConfigPath, configPaths.connectionsConfigPath, localization)
      }
      case None => println("MainGUI: guiActor not defined")
    }
  }

  private def openGlobalEditor(): Unit = {
    postInitData match {
      case Some(pa) => {
        new Logger(pa.guiActor).info("Editing global configuration file " + configPaths.globalConfigPath)
        GlobalEditorStarter.openGlobalEditor(pa.stage, configPaths, localization, pa.guiActor)
      }
      case None => println("MainGUI: guiActor not defined")
    }
  }
}
