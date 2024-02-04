package dbtarzan.gui

import org.apache.pekko.actor.ActorRef
import dbtarzan.gui.config.composite.CompositeEditorStarter
import dbtarzan.gui.config.connections.ConnectionEditorStarter
import dbtarzan.gui.config.global.GlobalEditorStarter
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.types.ConfigPath
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.JFXApp3
import scalafx.scene.control.{Menu, MenuBar}



class MainGUIMenu(stage: () => JFXApp3.PrimaryStage,
                  guiActor: ActorRef,
                  connectionsActor: ActorRef,
                  log: Logger,
                  configPaths: ConfigPath,
                  localization: Localization,
                  encryptionKeyExtractor: EncryptionKeyExtractor,
                  global: Global) {

  def buildMenu(): MenuBar = new MenuBar {
    menus = List(
      buildSettingsMenu(),
      buildHelpMenu()
    )
  }

  private def buildSettingsMenu() = new Menu(localization.settings) {
    items = List(
      JFXUtil.menuItem(localization.globalSettings, () => openGlobalEditor()),
      JFXUtil.menuItem(localization.editConnections, () => openConnectionsEditor()),
      JFXUtil.menuItem(localization.editComposites, () => openCompositeEditor())
    )
  }

  private def buildHelpMenu() = new Menu(localization.help) {
    items = List(
      JFXUtil.menuItem(localization.documentation, () => OpenWeb.openWeb("https://aferrandi.github.io/dbtarzan/")),
    )
  }

  private def openConnectionsEditor(): Unit = {
      log.info(localization.editingConnectionFile(configPaths.connectionsConfigPath))
      encryptionKeyExtractor.extractEncryptionKey(stage()) match {
        case Some(key) =>
          global.setConnectionEditor(ConnectionEditorStarter.openConnectionsEditor(stage(), connectionsActor, configPaths.connectionsConfigPath, key, localization))
        case None => println("MainGUI: encryptionKey not entered")
      }
  }

  private def openCompositeEditor(): Unit = {
      log.info(localization.editingCompositeFile(configPaths.compositeConfigPath))
      CompositeEditorStarter.openCompositeEditor(stage(), configPaths.compositeConfigPath, configPaths.connectionsConfigPath,  connectionsActor, localization)
  }

  private def openGlobalEditor(): Unit = {
      log.info("Editing global configuration file " + configPaths.globalConfigPath)
      GlobalEditorStarter.openGlobalEditor(stage(), configPaths, localization, guiActor)
  }
}
