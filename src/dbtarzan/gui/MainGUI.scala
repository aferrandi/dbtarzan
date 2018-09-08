package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, MenuItem, Menu, MenuBar }
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.stage.Screen
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Orientation
import scalafx.scene.layout.BorderPane
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.config.ConnectionEditorStarter
import dbtarzan.types.ConfigPath
import dbtarzan.messages.Logger




/* the main GUI of dbtarzan. database list on the left, menu on the top, the rest in the middle.
	the actors are still not been created when calling the constructor, therefore they are passed as functions.
 */
class MainGUI(
	guiWorker: => ActorRef,
	configActor : => ActorRef, 
	connectonsConfigPath: ConfigPath, 
	version: String, 
	openWeb : String => Unit, 
	closeApp : () => Unit)
{
	/* the database tabs on the middle-right side */
	val databaseTabs = new DatabaseTabs(guiWorker, configActor)
	/* the log/error list on the bottom */
	val logList = new LogList()
	/* the database/connection list on the left side */
	val databaseList = new DatabaseList()
	/* how big is the screen */
	private val screenBounds = Screen.primary.visualBounds
	/* the gui */
	private val stage = buildStage()

	def onDatabaseSelected(use : String => Unit) : Unit = databaseList.onDatabaseSelected(use)

	def onForeignKeyToFile(use : String => Unit) : Unit = databaseList.onForeignKeyToFile(use)

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

	private def buildMenu() = new MenuBar {
		menus = List(
		  new Menu("Connections") {
		    items = List(
		      new MenuItem("Edit Connections") {
		        onAction = {
		          e: ActionEvent => {
									new Logger(guiWorker).info("Editing connections configuration file "+connectonsConfigPath.path)
								ConnectionEditorStarter.openConnectionsEditor(stage, configActor, connectonsConfigPath, openWeb)
							}
		        }
		      }
		    )
		  },
		  new Menu("Help") {
		    items = List(
		      new MenuItem("Documentation") {
		        onAction = {		        	
		          e: ActionEvent =>  openWeb("http://github.com/aferrandi/dbtarzan/wiki") 
		        }
		      }
		    )
		  }
		)
	}

	private def buildMainView() = new BorderPane {
		top = buildMenu() 
		center = mainSplitPane()
	}

    private def buildDatabaseSplitPane() = new SplitPane {
		val databaseListWithTitle = JFXUtil.withTitle(databaseList.control, "Databases")
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
}