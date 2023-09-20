package dbtarzan.gui.rowdetails

import dbtarzan.db.{Field, FieldType}
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import dbtarzan.types.Binaries.Binary
import scalafx.scene.Node
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

class RowDetailsCell(field: Field, localization: Localization, log: Logger) {
  private val cell = field.fieldType match {
    case FieldType.STRING | FieldType.BINARY => new RowDetailsCellText(field, localization, log)
    case _ => new RowDetailsCellNumber(field)
  }

  val content: VBox = new VBox {
    children = List(new Label(field.name), cell.control)
    fillWidth = true
  }

  def showText(value: String | Int | Double| Binary): Unit = {
    cell.showText(value)
  }
}
