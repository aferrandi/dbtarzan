package dbtarzan.gui.foreignkeys

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ TableNames, DatabaseId }
import dbtarzan.messages.RequestAdditionalForeignKeys
import dbtarzan.localization.Localization

/* to start the connection editor. It handles all the cancel/closing/save events */
object AdditionalForeignKeysEditorStarter
{
 def openAdditionalForeignKeysEditor(
    parentStage : Stage, 
    dbActor : ActorRef,
    guiActor: ActorRef, 
    databaseId: DatabaseId,
    tableNames: TableNames,
    localization: Localization) : AdditionalForeignKeysEditor = {
    println("open additional foreign keys editor")  
    val editor = new AdditionalForeignKeysEditor(dbActor, guiActor, databaseId, tableNames, localization)
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