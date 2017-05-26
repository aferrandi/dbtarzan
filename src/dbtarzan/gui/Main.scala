package dbtarzan.gui

import java.io.File
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer 
import scalafx.stage.{ Screen, Stage, StageStyle, WindowEvent }
import scalafx.scene.layout.GridPane
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.Includes._
import scala.util.{Try, Success, Failure}
import dbtarzan.db.ConnectionBuilder
import dbtarzan.config.ConnectionDataReader
import akka.actor.{ ActorSystem, Props, ActorRef }
import dbtarzan.gui.actor.GUIWorker
import dbtarzan.config.actor.ConnectionsWorker
import dbtarzan.types.ConfigPath
import dbtarzan.messages.{ QueryTables, QueryDatabase, CopyToFile, DatabaseNames, ConnectionDatas, Info }
import java.nio.file.{ Path, Paths }

/**
  Main class, starts the main gui, the actors, and connects them together
*/
object Main extends JFXApp {
  println("Named commend line arguments:"+ parameters.named.mkString(","))
  val version = versionFromManifest()
  val system = ActorSystem("Sys")
  var configsPath = parameters.named.getOrElse("configPath", ".")
  val generalConfigPath = ConfigPath(Paths.get(configsPath, "general.config"))
  val connectionsConfigPath = ConfigPath(Paths.get(configsPath, "connections.config"))
  println("generalConfigPath:"+generalConfigPath+" connectionsConfigPath:"+connectionsConfigPath.path)
  val connections = ConnectionDataReader.read(connectionsConfigPath)
  val mainGUI = new MainGUI(_guiActor, _configActor, connectionsConfigPath, version, openWeb, closeApp)
  val guiActor = system.actorOf(Props(new GUIWorker(mainGUI.databaseTabs, mainGUI.logList, mainGUI.databaseList)).withDispatcher("my-pinned-dispatcher"), "guiWorker")
  val configActor = system.actorOf(Props(new ConnectionsWorker(ConnectionDatas(connections), guiActor)).withDispatcher("my-pinned-dispatcher"), "configWorker")
  mainGUI.databaseList.setDatabases(DatabaseNames(connections.map(_.name)))
  mainGUI.onDatabaseSelected( { case databaseName => {
    guiActor ! Info("Opening database "+databaseName)
    configActor ! QueryDatabase(databaseName) 
    }})
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

