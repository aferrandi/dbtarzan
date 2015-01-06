package dbtarzan.gui

import java.io.File
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer 
import scalafx.scene.Scene
import scalafx.stage.Screen
import scalafx.scene.control.SplitPane
import scalafx.scene.image.Image
import scalafx.scene.layout.GridPane
import scalafx.geometry.Orientation
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.Includes._
import scala.util.{Try, Success, Failure}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.config.{ Config, ConfigReader }
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import dbtarzan.gui.actor.GUIWorker
import dbtarzan.messages.QueryTables

/**
  Main class, containing everything
*/
object Main extends JFXApp {
  val system = ActorSystem("Sys")
  val databaseTabs = new DatabaseTabs()
  val errorList = new ErrorList()
  val guiActor = system.actorOf(Props(new GUIWorker(databaseTabs, errorList)).withDispatcher("my-pinned-dispatcher"), "guiworker")
  val databaseList = new DatabaseList(guiActor)
  databaseList.onDatabaseSelected( { case (databaseName, dbActor) => addDatabase(databaseName, dbActor) })

  val screenBounds = Screen.primary.visualBounds
  stage = buildStage()

  def addDatabase(databaseName : String, dbActor : ActorRef ) : Unit = {
    val database = new Database(dbActor, databaseName)
    databaseTabs.addDatabase(database)
    dbActor ! QueryTables(database.id)
  }
  private def buildDatabaseSplitPane() = new SplitPane {
      items.addAll(JFXUtil.withTitle(databaseList.list, "Databases"), databaseTabs.tabs)
      dividerPositions = 0.2
    }
  private def mainSplitPane() = new SplitPane {
        orientation() =  Orientation.VERTICAL
        items.addAll(buildDatabaseSplitPane(), errorList.list)
        dividerPositions = 0.85
      }

  def buildStage() = new PrimaryStage {
    title = "DbTarzan"
    icons.add(new Image(getClass().getResourceAsStream("monkey-face-cartoon.png")))
    scene = new Scene(screenBounds.width / 2, screenBounds.height / 2 ) {
      root = mainSplitPane()
      onCloseRequest = handle {
        println("Stage is closing")
        system.shutdown()
        System.exit(0)
      }
    }
  }
}

