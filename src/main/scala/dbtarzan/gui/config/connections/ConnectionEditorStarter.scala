package dbtarzan.gui.config.connections

import scalafx.stage.{Stage, StageStyle, WindowEvent}
import scalafx.scene.Scene
import scalafx.Includes.*
import org.apache.pekko.actor.ActorRef

import java.nio.file.Path
import dbtarzan.config.connections.{ConnectionData, ConnectionDataReader, ConnectionDataWriter}
import dbtarzan.messages.{ConnectionDatas, ExtractSchemas, TestConnection}
import dbtarzan.config.password.EncryptionKey
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger

/* to start the connection editor. It handles all the cancel/closing/save events */
object ConnectionEditorStarter
{
 def openConnectionsEditor(
          parentStage : Stage,
          connectionsActor : ActorRef,
          configPath: Path,
          encryptionKey : EncryptionKey,
          localization: Localization,
          log: Logger) : ConnectionEditor = {
    val connectionData: List[ConnectionData] = ConnectionDataReader.read(configPath)
    val editor = new ConnectionEditor(connectionData, encryptionKey, localization, log)
    val connectionStage = new Stage {
      title = localization.editConnections
      width = 800
      height = 700
      scene = new Scene {
        def onSave(connectionsToSave: List[ConnectionData]) : Unit = {
            log.debug(s"Saving the connections ${connectionsToSave.map(c => c.name).mkString(",")}")
            ConnectionDataWriter.write(configPath, connectionsToSave)
            connectionsActor ! ConnectionDatas(connectionsToSave)
            window().hide()
          }

        def onCancel() : Unit =
          window().hide()

        editor.onSave(onSave)
        editor.onCancel(() => onCancel())
        editor.onTestConnection((data, password) => connectionsActor ! TestConnection(data, encryptionKey, password))
        editor.onSchemasLoad((data, password) => connectionsActor ! ExtractSchemas(data, encryptionKey, password))
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