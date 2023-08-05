package dbtarzan.gui.orderby

import scalafx.stage.{ Stage, StageStyle, WindowEvent }
import scalafx.scene.Scene
import scalafx.Includes._

import dbtarzan.db.{  DBTable, DBTableStructure, OrderByFields }
import dbtarzan.localization.Localization

/* to start the order by editor. It handles all the cancel/closing/save events */
object OrderByEditorStarter
{
 def openOrderByEditor(parentStage : Stage, dbTable: DBTable, useNewTable : (DBTableStructure, Boolean) => Unit, localization : Localization) : Unit = {
     val orderByStage = new Stage {
      title = localization.chooseOrderByColumns
      width = 400
      height = 400
      scene = buildScene(dbTable, useNewTable, localization) 
      onCloseRequest = (event : WindowEvent) => { 
        event.consume()
        scene().window().hide()
      }
    }
    orderByStage.initOwner(parentStage)    
    orderByStage.initStyle(StageStyle.Utility)
    orderByStage.show()
  }

  private def buildScene(dbTable: DBTable, useNewTable : (DBTableStructure, Boolean) => Unit, localization : Localization) = new Scene {
      def onSave(orderByFields: OrderByFields) : Unit = {
        useNewTable(dbTable.withOrderByFields(orderByFields), false)
        window().hide()
      }

      def onCancel() : Unit = {
        window().hide()
      }

      val editor = new OrderByEditor(dbTable.fields, dbTable.orderBys, onSave(_), () => onCancel(), localization)

      root = editor.control
  }
}