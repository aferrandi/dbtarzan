package dbtarzan.gui

import java.io.File
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer 
import scalafx.stage.{ Screen, Stage, StageStyle, WindowEvent }
import scalafx.scene.layout.GridPane
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.Includes._
import scala.util.{Try, Success, Failure}
import dbtarzan.config.{ Config, ConfigReader }
import dbtarzan.db.ConnectionBuilder
import akka.actor.{ ActorSystem, Props, ActorRef }
import dbtarzan.gui.actor.GUIWorker
import dbtarzan.config.actor.ConfigWorker
import dbtarzan.messages.{ QueryTables, QueryDatabase, CopyToFile, DatabaseNames, ConnectionDatas }


/**
  Main class, starts the main gui, the actors, and connects them together
*/
object Main extends JFXApp {
  val version = "1.00"
  val system = ActorSystem("Sys")
  val connections = ConfigReader.read("connections.config")
  val mainGUI = new MainGUI(_guiActor, _configActor, version, closeApp)
  val guiActor = system.actorOf(Props(new GUIWorker(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList)).withDispatcher("my-pinned-dispatcher"), "guiWorker")
  val configActor = system.actorOf(Props(new ConfigWorker(ConnectionDatas(connections), guiActor)).withDispatcher("my-pinned-dispatcher"), "configWorker")
  mainGUI.databaseList.setDatabases(DatabaseNames(connections.map(_.name)))
  println("configWorker "+configActor)  
  mainGUI.onDatabaseSelected( { case databaseName => configActor ! QueryDatabase(databaseName) })
  mainGUI.onForeignKeyToFile( { case databaseName => configActor ! CopyToFile(databaseName) })

  def _guiActor() : ActorRef = guiActor
  def _configActor()  : ActorRef = configActor

  /* first we close the dbWorker actors. Then the config and gui actors. Then we stop the actor system and we check that there are no more actors. 
      Once this is done, we close JavaF (the GUI)
  */
  private def closeApp() : Unit = {
      println("application exit")
      import akka.pattern.gracefulStop
      import scala.concurrent._
      import scala.concurrent.duration._
      import ExecutionContext.Implicits.global
      val stopAll = for {
        stopGui : Boolean <- gracefulStop(guiActor, 1 seconds)
        stopConfig : Boolean <- gracefulStop(configActor, 1 seconds)
      } yield stopGui && stopConfig
      stopAll.foreach(x => { 
        system.shutdown()
        println("shutdown")
        system.registerOnTermination(() => scalafx.application.Platform.exit())
      })
  }
}

