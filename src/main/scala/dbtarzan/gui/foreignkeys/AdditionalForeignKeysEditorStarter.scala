package dbtarzan.gui.foreignkeys

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import org.apache.pekko.actor.ActorRef

import dbtarzan.db.{ TableId, DatabaseId }
import dbtarzan.messages.RequestAdditionalForeignKeys
import dbtarzan.localization.Localization
import dbtarzan.gui.util.TableIdLabel

/* to start the additional foreign keys editor. It handles all the closing events. The other events are handled by the editor itself */
object AdditionalForeignKeysEditorStarter
{
 def openAdditionalForeignKeysEditor(
                                      parentStage : Stage,
                                      dbActor : ActorRef,
                                      guiActor: ActorRef,
                                      databaseId: DatabaseId,
                                      tableIds: List[TableId],
                                      localization: Localization) : AdditionalForeignKeysEditor = {
    println("open additional foreign keys editor")  
    val editor = new AdditionalForeignKeysEditor(dbActor, guiActor, databaseId, tableIds.sortBy(TableIdLabel.toLabel), localization)
    val additionalForeignKeysStage = new Stage {
      title = localization.openAdditionalForeignKeys
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
    additionalForeignKeysStage.initOwner(parentStage)    
    additionalForeignKeysStage.initStyle(StageStyle.UTILITY)
    additionalForeignKeysStage.show()
    dbActor ! RequestAdditionalForeignKeys(databaseId)
    editor
  }
}