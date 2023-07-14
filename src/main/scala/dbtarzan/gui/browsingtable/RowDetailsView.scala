package dbtarzan.gui.browsingtable

import dbtarzan.db.{DBTable, Row}
import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control.ScrollPane
import scalafx.scene.layout.VBox

/* displays one single line of the table, as a vertical list of the fields */
class RowDetailsView(dbTable : DBTable) extends TControlBuilder {
    private val names = dbTable.fields
    /* the cell components */
    private val cells : List[RowDetailsCell] = names.map({ case (field) => new RowDetailsCell(field)})

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

    def control : Parent = cellsContainer
}