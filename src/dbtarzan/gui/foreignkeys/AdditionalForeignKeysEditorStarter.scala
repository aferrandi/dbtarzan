package dbtarzan.gui.foreignkeys

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ TableNames, ForeignKeysForTableList, ForeignKey }
import dbtarzan.localization.Localization

/* to start the connection editor. It handles all the cancel/closing/save events */
object AdditionalForeignKeysEditorStarter
{
 def openAdditionalForeignKeysEditor(
    parentStage : Stage, 
    dbWorker : ActorRef,
    guiActor: ActorRef, 
    localization: Localization) : AdditionalForeignKeysEditor = {
    println("open additional foreign keys editor")  
    val editor = new AdditionalForeignKeysEditor(guiActor, localization)
    val additionalForeignKeysStage = new Stage {
      title = localization.editConnections
      width = 800
      height = 600
      scene = new Scene {
        def onSave(additionalKeys: List[ForeignKey]) : Unit = {
            dbWorker !
            window().hide()
          }

        def onCancel() : Unit = 
          window().hide()

        editor.onSave(onSave(_))
        editor.onCancel(() => onCancel())
        onCloseRequest = (event : WindowEvent) => { 
          event.consume()
          editor.cancelIfPossible(() => onCancel()) 
          }
        root = editor.control
      }
    }
    additionalForeignKeysStage.initOwner(parentStage)    
    additionalForeignKeysStage.initStyle(StageStyle.UTILITY)
    additionalForeignKeysStage.show()
    editor
  }
}