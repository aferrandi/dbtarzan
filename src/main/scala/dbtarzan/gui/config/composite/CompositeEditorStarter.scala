package dbtarzan.gui.config.composite

import akka.actor.ActorRef
import dbtarzan.config.composite.{CompositeReader, CompositeWriter}
import dbtarzan.config.connections.{ConnectionData, ConnectionDataReader}
import dbtarzan.db.{Composite, SimpleDatabaseId}
import dbtarzan.localization.Localization
import dbtarzan.messages.Composites
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.stage.{Stage, StageStyle, WindowEvent}

import java.nio.file.Path

/* to start the connection editor. It handles all the cancel/closing/save events */
object CompositeEditorStarter
{
 def openCompositeEditor(
    parentStage : Stage,
    compositeConfigPath: Path,
    connectionConfigPath: Path,
    connectionsActor : ActorRef,
    localization: Localization) : CompositeEditor = {
    val connectionData: List[ConnectionData] = ConnectionDataReader.read(connectionConfigPath)
    val composites: List[Composite] = CompositeReader.read(compositeConfigPath)
    val editor = new CompositeEditor(composites, connectionData.map(cd => SimpleDatabaseId(cd.name)), localization)
    val compositeStage = new Stage {
      title = localization.editComposites
      width = 800
      height = 600
      scene = new Scene {
        def onSave(compositesToSave: List[Composite]) : Unit = {
            CompositeWriter.write(compositeConfigPath, compositesToSave)
            connectionsActor ! Composites(compositesToSave)
            window().hide()
          }

        def onCancel() : Unit = 
          window().hide()

        editor.onSave(onSave)
        editor.onCancel(() => onCancel())
        onCloseRequest = (event : WindowEvent) => {
          event.consume()
          editor.cancelIfPossible(() => onCancel()) 
          }
        root = editor.control
      }
    }
    compositeStage.initOwner(parentStage)
    compositeStage.initStyle(StageStyle.Utility)
    compositeStage.show()
    editor
  }
}