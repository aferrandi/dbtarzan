package dbtarzan.gui.config.connections

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import akka.actor.ActorRef
import java.nio.file.Path

import dbtarzan.config.connections.{ ConnectionDataReader, ConnectionDataWriter, ConnectionData }
import dbtarzan.messages.ConnectionDatas
import dbtarzan.config.EncryptionKey
import dbtarzan.localization.Localization

/* to start the connection editor. It handles all the cancel/closing/save events */
object ConnectionEditorStarter
{
 def openConnectionsEditor(
    parentStage : Stage, 
    connectionsWorker : ActorRef, 
    configPath: Path, 
    openWeb : String => Unit, 
    encryptionKey : EncryptionKey,
    localization: Localization) : Unit = {
    println("open connections editor")  
    val connectionStage = new Stage {
      title = localization.editConnections
      width = 800
      height = 600
      scene = new Scene {
        def onSave(connectionsToSave: List[ConnectionData]) : Unit = {
            ConnectionDataWriter.write(configPath, connectionsToSave)
            connectionsWorker ! ConnectionDatas(connectionsToSave)
            window().hide()
          }

        def onCancel() : Unit = 
          window().hide()

        val editor = new ConnectionEditor(ConnectionDataReader.read(configPath), openWeb, encryptionKey, localization)
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