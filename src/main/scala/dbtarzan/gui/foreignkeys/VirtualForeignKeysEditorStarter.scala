package dbtarzan.gui.foreignkeys

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import org.apache.pekko.actor.ActorRef

import dbtarzan.db.{ TableId, DatabaseId }
import dbtarzan.messages.RequestVirtualForeignKeys
import dbtarzan.localization.Localization
import dbtarzan.gui.util.TableIdLabel

/* to start the virtual foreign keys editor. It handles all the closing events. The other events are handled by the editor itself */
object VirtualForeignKeysEditorStarter
{
 def openVirtualForeignKeysEditor(
                                      parentStage : Stage,
                                      dbActor : ActorRef,
                                      guiActor: ActorRef,
                                      databaseId: DatabaseId,
                                      tableIds: List[TableId],
                                      localization: Localization) : VirtualForeignKeysEditor = {
    println("open virtual foreign keys editor")
    val editor = new VirtualForeignKeysEditor(dbActor, guiActor, databaseId, tableIds.sortBy(TableIdLabel.toLabel), localization)
    val virtualForeignKeysStage = new Stage {
      title = localization.openVirtualForeignKeys
      width = 800
      height = 600
      scene = new Scene {

        def onClose() : Unit = 
          window().hide()

        editor.onClose(() => onClose())
        onCloseRequest = (event : WindowEvent) => { 
          event.consume()
          editor.cancelIfPossible(() => onClose()) 
          }
        root = editor.control
      }
    }
    virtualForeignKeysStage.initOwner(parentStage)
    virtualForeignKeysStage.initStyle(StageStyle.Utility)
    virtualForeignKeysStage.show()
    editor
  }
}