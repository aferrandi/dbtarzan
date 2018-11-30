package dbtarzan.gui.orderby

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._

import dbtarzan.db.{  DBTable, DBTableStructure, OrderByFields }
/* to start the order by editor. It handles all the cancel/closing/save events */
object OrderByEditorStarter
{
 def openOrderByEditor(parentStage : Stage, dbTable: DBTable, useNewTable : DBTableStructure => Unit) : Unit = {
     val orderByStage = new Stage {
      title = "Choose OrderBy Columns"
      width = 400
      height = 400
      scene = buildScene(dbTable, useNewTable) 
      onCloseRequest = (event : WindowEvent) => { 
        event.consume()
        scene.window().hide()
      }
    }
    orderByStage.initOwner(parentStage)    
    orderByStage.initStyle(StageStyle.UTILITY)
    orderByStage.show()
  }

  private def buildScene(dbTable: DBTable, useNewTable : DBTableStructure => Unit) = new Scene {
      def onSave(orderByFields: OrderByFields) : Unit = {
        useNewTable(dbTable.withOrderByFields(orderByFields))
        window().hide()
      }

      def onCancel() : Unit = {
        println("cancel")
        window().hide()
      }

      val editor = new OrderByEditor(dbTable.columnNames, dbTable.orderBys, onSave(_), () => onCancel())

      root = editor.control
  }
}