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
import dbtarzan.types.ConfigPath
import dbtarzan.messages.{ QueryTables, QueryDatabase, CopyToFile, DatabaseNames, ConnectionDatas }
import java.nio.file.{ Path, Paths }

/**
  Main class, starts the main gui, the actors, and connects them together
*/
object Main extends JFXApp {
  println("Named commend line arguments:"+ parameters.named.mkString(","))
  val version = versionFromManifest()
  val system = ActorSystem("Sys")
  val connectionsConfigPath = ConfigPath(Paths.get(parameters.named.getOrElse("configPath", "."), "connections.config"))
  println("connectionsConfigPath:"+connectionsConfigPath.path)
  val connections = ConfigReader.read(connectionsConfigPath)
  val mainGUI = new MainGUI(_guiActor, _configActor, connectionsConfigPath, version, openWeb, closeApp)
  val guiActor = system.actorOf(Props(new GUIWorker(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList)).withDispatcher("my-pinned-dispatcher"), "guiWorker")
  val configActor = system.actorOf(Props(new ConfigWorker(ConnectionDatas(connections), guiActor)).withDispatcher("my-pinned-dispatcher"), "configWorker")
  mainGUI.databaseList.setDatabases(DatabaseNames(connections.map(_.name)))
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

  private def openWeb(url : String) : Unit = try
  {  hostServices.showDocument(url) }
  catch {
    case e: Throwable =>  new ProcessBuilder("x-www-browser", url).start()
  }
  

  private def versionFromManifest() = Option(getClass().getPackage().getImplementationVersion()).getOrElse("")
}

