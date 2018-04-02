package dbtarzan.gui.orderby

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.types.ConfigPath
import dbtarzan.db.{  OrderByFields }
/* to start the order by editor. It handles all the cancel/closing/save events */
object OrderByEditorStarter
{
 def openOrderByEditor(parentStage : Stage, table: dbtarzan.db.Table, useNewTable : dbtarzan.db.Table => Unit) : Unit = {
     val orderByStage = new Stage {
      title = "Choose OrderBy Columns"
      width = 400
      height = 400
      scene = new Scene {
        def onSave(orderByFields: OrderByFields) : Unit = {
            useNewTable(table.withOrderByFields(orderByFields))
            window().hide()
          }

        def onCancel() : Unit = {
          println("cancel")
          window().hide()
        }

        val editor = new OrderByEditor(table.columnNames, table.orderBys, onSave(_), () => onCancel())
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