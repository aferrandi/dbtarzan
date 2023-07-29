package dbtarzan.gui.config.connections

import scalafx.stage.{Stage, StageStyle, WindowEvent}
import scalafx.scene.Scene
import scalafx.Includes._
import org.apache.pekko.actor.ActorRef
import java.nio.file.Path

import dbtarzan.config.connections.{ConnectionData, ConnectionDataReader, ConnectionDataWriter}
import dbtarzan.messages.{ConnectionDatas, ExtractSchemas, TestConnection}
import dbtarzan.config.password.EncryptionKey
import dbtarzan.localization.Localization

/* to start the connection editor. It handles all the cancel/closing/save events */
object ConnectionEditorStarter
{
 def openConnectionsEditor(
          parentStage : Stage,
          connectionsActor : ActorRef,
          configPath: Path,
          encryptionKey : EncryptionKey,
          localization: Localization) : ConnectionEditor = {
    val connectionData: List[ConnectionData] = ConnectionDataReader.read(configPath)
    val editor = new ConnectionEditor(connectionData, encryptionKey, localization)
    val connectionStage = new Stage {
      title = localization.editConnections
      width = 800
      height = 600
      scene = new Scene {
        def onSave(connectionsToSave: List[ConnectionData]) : Unit = {
            ConnectionDataWriter.write(configPath, connectionsToSave)
            connectionsActor ! ConnectionDatas(connectionsToSave)
            window().hide()
          }

        def onCancel() : Unit =
          window().hide()

        editor.onSave(onSave)
        editor.onCancel(() => onCancel())
        editor.onTestConnection(data => connectionsActor ! TestConnection(data, encryptionKey))
        editor.onSchemasLoad(data => connectionsActor ! ExtractSchemas(data, encryptionKey))
        onCloseRequest = (event : WindowEvent) => {
          event.consume()
          editor.cancelIfPossible(() => onCancel()) 
          }
        root = editor.control
      }
    }
    connectionStage.initOwner(parentStage)    
    connectionStage.initStyle(StageStyle.Utility)
    connectionStage.show()
    editor
  }
}