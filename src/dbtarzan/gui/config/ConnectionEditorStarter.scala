package dbtarzan.gui.config

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import dbtarzan.config.{ ConnectionDataReader, ConnectionDataWriter, ConnectionData }
import akka.actor.ActorRef
import dbtarzan.messages.ConnectionDatas
import dbtarzan.types.ConfigPath

/* to start the connection editor. It handles all the cancel/closing/save events */
object ConnectionEditorStarter
{
 def openConnectionsEditor(parentStage : Stage, connectionsWorker : ActorRef, connectionsConfigPath: ConfigPath, openWeb : String => Unit) : Unit = {

    println("open connections editor")  
     val connectionStage = new Stage {
      title = "Edit Connections"
      width = 800
      height = 600
      scene = new Scene {
        def onSave(connectionsToSave: List[ConnectionData]) : Unit = {
            ConnectionDataWriter.write(connectionsConfigPath, connectionsToSave)
            connectionsWorker ! ConnectionDatas(connectionsToSave)
            window().hide()
          }

        def onCancel() : Unit = 
          window().hide()

        val editor = new ConnectionEditor(ConnectionDataReader.read(connectionsConfigPath), openWeb)
        editor.onSave(onSave(_))
        editor.onCancel(() => onCancel())
        onCloseRequest = (event : WindowEvent) => { 
          event.consume()
          editor.cancelIfPossible(() => onCancel()) 
          }
        root = editor.control
      }
    }
    connectionStage.initOwner(parentStage)    
    connectionStage.initStyle(StageStyle.UTILITY)
    connectionStage.show()
  }

}