package dbtarzan.gui.rowdetails

import dbtarzan.db.{DBTable, Field, Row}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control.ScrollPane
import scalafx.scene.layout.VBox

class RowDetailsViewFields(dbTable : DBTable, localization: Localization, log: Logger) extends TControlBuilder {
  private val names: List[Field] = dbTable.fields
  /* the cell components */
  private val cells : List[RowDetailsCell] = names.map({ case (field) => new RowDetailsCell(field, localization, log)})

  private val cellsContainer = buildCellsContainer()

  private def buildCellsContainer() = new ScrollPane {
    content = new VBox {
      padding = Insets(5)
      spacing = 5
      alignment = Pos.TopLeft
      fillWidth = true
      children = cells.map(c => c.content)
    }
    hbarPolicy = ScrollPane.ScrollBarPolicy.Never
    /* need a vertcal scrollbar to show all the fields if the row is very long */
    vbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
    fitToWidth = true
  }

  def displayRow(row : Row) : Unit = {
    row.values.zip(cells).foreach({ case (value, cell) => cell.showText(value)})
  }

  def filterFields(text: String): Unit = {
    val textLower = text.toLowerCase
    names.zip(cells).foreach(
      {  case (name, cell) =>
        val visible = name.name.toLowerCase.contains(textLower)
        changeFieldVisibility(cell, visible)
      }
    )
  }

  private def changeFieldVisibility(cell: RowDetailsCell, visible: Boolean): Unit = {
    JFXUtil.changeVBoxItemVisibility(cell.content,visible)
  }

  def control : Parent = cellsContainer
}
