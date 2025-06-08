package dbtarzan.gui.info

import dbtarzan.db.QuerySql
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.{Button, Label, TextArea}
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.Includes._


/** The read only text box showing the query sql, so that it can be seen anc copied */
class QueryInfo(sql : QuerySql, localization : Localization, requestRowsNumber : () => Unit) extends TControlBuilder {
  private val textBox = new TextArea {
    text = sql.sql
    editable = false
    wrapText = true
  }

  private val buttonRowsNumber = new Button {
    text = localization.rowsNumber
    onAction = (_: ActionEvent) => { requestRowsNumber() }
  }

  private val labelRowsNumber = new Label {
    text = ""
  }

  private val layout = new BorderPane {
    center = textBox
    bottom = new HBox {
      children = List(
        buttonRowsNumber,
        labelRowsNumber,
      )
      spacing = 10
    }
  }

  def control : Parent = layout

  def showRowsNumber(rowsNumber: Int): Unit = {
    labelRowsNumber.text = rowsNumber.toString
    buttonRowsNumber.disable = true
  }
}
