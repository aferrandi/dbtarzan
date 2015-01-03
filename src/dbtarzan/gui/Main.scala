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
  val guiActor = system.actorOf(Props(new GUIWorker(databaseTabs)).withDispatcher("my-pinned-dispatcher"), "guiworker")
  val databaseList = new DatabaseList(guiActor)
  databaseList.onDatabaseSelected( { case (databaseName, dbActor) => addDatabase(databaseName, dbActor) })
  val screenBounds = Screen.primary.visualBounds
  stage = buildStage()

  def addDatabase(databaseName : String, dbActor : ActorRef ) : Unit = {
    val database = new Database(dbActor, databaseName)
    databaseTabs.addDatabase(database)
    dbActor ! QueryTables(database.id)
  }
  def buildStage() = new PrimaryStage {
    title = "DbTarzan"
    width = screenBounds.width / 2
    height = screenBounds.height / 2   
    icons.add(new Image(getClass().getResourceAsStream("monkey-face-cartoon.png")))

    scene = new Scene {
      root = new SplitPane {
        maxHeight = Double.MaxValue
        maxWidth = Double.MaxValue
        items.addAll(JFXUtil.withTitle(databaseList.list, "Databases"), databaseTabs.tabs)
        dividerPositions = 0.2
      }
      onCloseRequest = handle {
        println("Stage is closing")
        system.shutdown()
        System.exit(0)
      }
    }
  }
}

