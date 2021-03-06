package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.config.password.{EncryptionKey, PasswordEncryption, VerificationKey}
import dbtarzan.db.DatabaseId
import dbtarzan.gui.browsingtable.TableMenu
import dbtarzan.gui.config.connections.ConnectionEditorStarter
import dbtarzan.gui.config.global.GlobalEditorStarter
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.types.ConfigPath
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, SplitPane}
import scalafx.scene.image.Image
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.BorderPane
import scalafx.stage.Screen

/* the main GUI of dbtarzan. database list on the left, menu on the top, the rest in the middle.
	the actors are still not been created when calling the constructor, therefore they are passed as functions.
 */
class MainGUI(
	configPaths: ConfigPath, 
	localization: Localization,
	verificationKey: Option[VerificationKey],
	version: String, 
	openWeb : String => Unit, 
	closeApp : () => Unit)
{
	/* the database tabs on the middle-right side */
	val databaseTabs = new DatabaseTabs(localization)
	/* the log/error list on the bottom */
	val logList = new LogList(localization)
	/* the database/connection list on the left side */
	val databaseList = new DatabaseList(localization)

  val global = new Global();
	/* how big is the screen */
	private val screenBounds = Screen.primary.visualBounds
	/* the gui */
	private val stage = buildStage() 
	private var guiActor: Option[ActorRef]  = None
	private var connectionsActor: Option[ActorRef] = None
	private var encryptionKey : Option[EncryptionKey] = None
	private val encryptionKeyDialog  = new EncryptionKeyDialog(stage, localization)

	stage.scene().onKeyReleased = (ev: KeyEvent) => { handleShortcut(ev) }

  def setActors(guiActor: ActorRef, connectionsActor: ActorRef) : Unit = {
		this.guiActor = Some(guiActor)
		this.connectionsActor = Some(connectionsActor)
		databaseTabs.setActors(guiActor, connectionsActor)
  } 

	private def withExtractedEncryptionKey(use : EncryptionKey => Unit) : Unit = 
		extractEncryptionKey().foreach(use) 

	def onDatabaseSelected(use : (DatabaseId, EncryptionKey) => Unit) : Unit = 
		databaseList.onDatabaseSelected(databaseId => {
      if(!databaseTabs.showDatabase(databaseId))
        withExtractedEncryptionKey(encryptionKey => use(databaseId, encryptionKey))
      }
    )

	def onForeignKeyToFile(use : (DatabaseId, EncryptionKey) => Unit) : Unit = 
		databaseList.onForeignKeyToFile(databaseId => withExtractedEncryptionKey(encryptionKey => use(databaseId, encryptionKey)))

	private def buildStage() : PrimaryStage = new PrimaryStage {
	    title = "DbTarzan "+version
	    icons.add(appIcon())
	    scene = new Scene(screenBounds.width / 2, screenBounds.height / 2 ) {
	        root = buildMainView()
	        onCloseRequest = handle { 
	        	databaseTabs.sendCloseToAllOpen()
	        	closeApp() 
	        }
		  }
	}

	private def handleShortcut(ev : KeyEvent) : Unit = 
		guiActor match {
			case Some(ga) => TableMenu.handleKeyCombination(ga, ev, () => databaseTabs.currentTableId)
			case None => println("MainGUI: guiActor not defined")
		}
	
	private def buildMenu() = new MenuBar {
		menus = List(
			buildSettingsMenu(),
		  buildHelpMenu()
		)
	}

	private def buildSettingsMenu() = new Menu(localization.settings) {
    items = List(
      JFXUtil.menuItem(localization.globalSettings, openGlobalEditor),
      JFXUtil.menuItem(localization.editConnections, openConnectionsEditor)
    )
  }

	private def buildHelpMenu() = new Menu(localization.help) {
    items = List(
      JFXUtil.menuItem(localization.documentation, () => openWeb("https://aferrandi.github.io/dbtarzan/")),
    )
  }

	private def buildMainView() = new BorderPane {
		top = buildMenu() 
		center = mainSplitPane()
	}

  private def buildDatabaseSplitPane() = new SplitPane {
		val databaseListWithTitle: BorderPane = JFXUtil.withTitle(databaseList.control, localization.databases)
		items.addAll(databaseListWithTitle, databaseTabs.control)
		dividerPositions = 0.2
		SplitPane.setResizableWithParent(databaseListWithTitle, false)
	}

	private def mainSplitPane() = new SplitPane {
		orientation() =  Orientation.VERTICAL
		items.addAll(buildDatabaseSplitPane(), logList.control)
		dividerPositions = 0.85
		SplitPane.setResizableWithParent(logList.control, false)
	}
  
	private def appIcon() = new Image(getClass.getResourceAsStream("monkey-face-cartoon.png"))

	private def openConnectionsEditor() : Unit = {
		guiActor match {
			case Some(ga) => new Logger(ga).info(localization.editingConnectionFile(configPaths.connectionsConfigPath))
			case None => println("MainGUI: guiActor not defined")
		}		 
		extractEncryptionKey() match {
			case Some(key) => connectionsActor match {
					case Some(ca) => global.setConnectionEditor(ConnectionEditorStarter.openConnectionsEditor(stage, ca, configPaths.connectionsConfigPath, openWeb, key, localization))
					case None => println("MainGUI: connectionsActor not defined") 
				}
			case None => println("MainGUI: encryptionKey not entered")
		}
	}

	private def extractEncryptionKey() : Option[EncryptionKey] = {
		if(encryptionKey.isEmpty)
			encryptionKey  = verificationKey.map(
				vkey => encryptionKeyDialog.showDialog(vkey)
				).getOrElse(
					Some(PasswordEncryption.defaultEncryptionKey)
					)
		encryptionKey
	}


	private def openGlobalEditor() : Unit = {
		guiActor match {
			case Some(ga) => {
				new Logger(ga).info("Editing global configuration file "+configPaths.globalConfigPath)
				GlobalEditorStarter.openGlobalEditor(stage, configPaths, localization, ga)
			}
			case None => println("MainGUI: guiActor not defined")
		}
	}
}