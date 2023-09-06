package dbtarzan.gui.rowdetails

import dbtarzan.db.{DBTable, Row}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{FilterText, JFXUtil}
import dbtarzan.localization.Localization
import dbtarzan.messages.QueryTablesByPattern
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control.ScrollPane
import scalafx.scene.layout.{BorderPane, VBox}

/* displays one single line of the table, as a vertical list of the fields */
class RowDetailsView(dbTable : DBTable, localization : Localization) extends TControlBuilder {
  private val rowDetailsViewFields = new RowDetailsViewFields(dbTable, localization)

  private val filterText = new FilterText(text => rowDetailsViewFields.filterFields(text), localization)

  private val pane = new BorderPane {
    top = JFXUtil.withLeftTitle(filterText.control, localization.filterFields)
    center = rowDetailsViewFields.control
  }

  def displayRow(row: Row): Unit = {
    rowDetailsViewFields.displayRow(row)
  }
    def control : Parent = pane
}