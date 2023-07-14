package dbtarzan.gui

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListCell, ListView}
import dbtarzan.db.{TableId, TableIds}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{JFXUtil, TableIdLabel}
import scalafx.scene.Parent

/* The list of tables to choose from */
class TableList(originalTableIds : List[TableId]) extends TControlBuilder {
  private val buffer = ObservableBuffer[TableId](originalTableIds.sortBy(TableIdLabel.toLabel))
  private val list = new ListView[TableId](buffer) {
    cellFactory = { _ => buildCell() }
  }

  def addTableNames(tableIds : TableIds) : Unit = {
    buffer.clear()
    buffer ++= tableIds.tableIds
  }

  private def buildCell() = new ListCell[TableId] {
    item.onChange { (_, _, _) =>
      text.value = Option(item.value).map(tableId => TableIdLabel.toLabel(tableId)).getOrElse("")
    }
  }

  def onTableSelected(useTable : TableId => Unit) : Unit = {
      JFXUtil.onAction(list, { (selectedTable : TableId, _) =>
        useTable(selectedTable)
        })
    }
  def control : Parent = list

  def tableIds: TableIds = TableIds(buffer.toList)
}

