package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, MenuItem, Menu, MenuBar }
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.input.KeyEvent
import scalafx.scene.image.Image
import scalafx.stage.Screen
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Orientation
import scalafx.scene.layout.BorderPane
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.config.connections.ConnectionEditorStarter
import dbtarzan.gui.config.global.GlobalEditorStarter
import dbtarzan.types.ConfigPath
import dbtarzan.messages.Logger
import dbtarzan.db.DatabaseId
import dbtarzan.localization.Localization



/* the main GUI of dbtarzan. database list on the left, menu on the top, the rest in the middle.
	the actors are still not been created when calling the constructor, therefore they are passed as functions.
 */
class MainGUI(
	configPaths: ConfigPath, 
	localization: Localization,
	version: String, 
	openWeb : String => Unit, 
	closeApp : () => Unit)
{
	/* the database tabs on the middle-right side */
	val databaseTabs = new DatabaseTabs(localization)
	/* the log/error list on the bottom */
	val logList = new LogList()
	/* the database/connection list on the left side */
	val databaseList = new DatabaseList()
	/* how big is the screen */
	private val screenBounds = Screen.primary.visualBounds
	/* the gui */
	private var stage = buildStage() 
	private var guiActor: Option[ActorRef]  = None
	private var connectionsActor: Option[ActorRef] = None 
	
	stage.scene().onKeyReleased = (ev: KeyEvent) => { handleShortcut(ev) }

  def setActors(guiActor: ActorRef, connectionsActor: ActorRef) : Unit = {
		this.guiActor = Some(guiActor)
		this.connectionsActor = Some(connectionsActor)
		databaseTabs.setActors(guiActor, connectionsActor)
  } 

	def onDatabaseSelected(use : DatabaseId => Unit) : Unit = databaseList.onDatabaseSelected(use)

	def onForeignKeyToFile(use : DatabaseId => Unit) : Unit = databaseList.onForeignKeyToFile(use)

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
		      new MenuItem(localization.globalSettings) {
		        onAction = {
		          e: ActionEvent => { openGlobalEditor()	}
		        }
		      },					
		      new MenuItem(localization.editConnections) {
		        onAction = {
		          e: ActionEvent => { openConnectionsEditor()	}
		        }
		      }
		    )
		  }

	private def buildHelpMenu() = new Menu(localization.help) {
		    items = List(
		      new MenuItem(localization.documentation) {
		        onAction = {		        	
		          e: ActionEvent =>  openWeb("https://aferrandi.github.io/dbtarzan/") 
		        }
		      }
		    )
		  }

	private def buildMainView() = new BorderPane {
		top = buildMenu() 
		center = mainSplitPane()
	}

  private def buildDatabaseSplitPane() = new SplitPane {
		val databaseListWithTitle = JFXUtil.withTitle(databaseList.control, localization.databases)
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
  
	private def appIcon() = new Image(getClass().getResourceAsStream("monkey-face-cartoon.png"))

	private def openConnectionsEditor() : Unit = {
		guiActor match {
			case Some(ga) => new Logger(ga).info("Editing connections configuration file "+configPaths.connectionsConfigPath)
			case None => println("MainGUI: guiActor not defined")
		}
		connectionsActor match {
			case Some(ca) => ConnectionEditorStarter.openConnectionsEditor(stage, ca, configPaths.connectionsConfigPath, openWeb, localization)
			case None => println("MainGUI: connectionsActor not defined") 
		}
	}

	private def openGlobalEditor() : Unit = {
		guiActor match {
			case Some(ga) => {
				new Logger(ga).info("Editing global configuration file "+configPaths.globalConfigPath)
				GlobalEditorStarter.openGlobalEditor(stage, configPaths.globalConfigPath, localization)
			}
			case None => println("MainGUI: guiActor not defined")
		}
	}
}