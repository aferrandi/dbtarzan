package dbtarzan.gui.config

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.types.ConfigPath
import dbtarzan.db.{ Table, OrderByFields }
/* to start the connection editor. It handles all the cancel/closing/save events */
object OrderByEditorStarter
{
 def openOrderByEditor(parentStage : Stage, table: Table, useTable : Table => Unit) : Unit = {
     val orderByStage = new Stage {
      title = "Choose OrderBy Columns"
      width = 800
      height = 600
      scene = new Scene {
        def onSave(orderByFields: OrderByFields) : Unit = {
            useTable(table.withOrderByFields(orderByFields))
            window().hide()
          }

        def onCancel() : Unit = 
          window().hide()

        val editor = new OrderByEditor(table.columnNames, table.orderBys)
        editor.onSave(onSave(_))
        editor.onCancel(() => onCancel())
        onCloseRequest = (event : WindowEvent) => { 
          event.consume()
          onCancel()
          }
        root = editor.control
      }
    }
    orderByStage.initOwner(parentStage)    
    orderByStage.initStyle(StageStyle.UTILITY)
    orderByStage.show()
  }

}