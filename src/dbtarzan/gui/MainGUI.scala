package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, MenuItem, Menu, MenuBar }
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.stage.{ Screen}
import scalafx.Includes._
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.layout.BorderPane
import dbtarzan.gui.config.ConnectionEditorStarter
import scalafx.event.ActionEvent
import scalafx.geometry.Orientation
import akka.actor.{ ActorSystem, ActorRef }




class MainGUI(system : ActorSystem, version: String, databases :List[String], closeApp : () => Unit)
{
	val databaseTabs = new DatabaseTabs(system)
	val logList = new LogList()
	val databaseList = new DatabaseList(databases)
	val screenBounds = Screen.primary.visualBounds
	val stage = buildStage()

	
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
		          e: ActionEvent => ConnectionEditorStarter.openConnectionsEditor(stage)
		        }
		      }
		    )
		  }
		)
	}

	def onDatabaseSelected(use : String => Unit) : Unit = databaseList.onDatabaseSelected(use)
	def onForeignKeyToFile(use : String => Unit) : Unit = databaseList.onForeignKeyToFile(use)


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
  

	private def appIcon() = 
		new Image(getClass().getResourceAsStream("monkey-face-cartoon.png"))
}