package dbtarzan.gui.database

import dbtarzan.db.{TableId, TableIds}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{JFXUtil, TableIdLabel}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.ListView

/* The list of tables to choose from */
class TableList(originalTableIds : List[TableId]) extends TControlBuilder {
  private val buffer : ObservableBuffer[TableId] = ObservableBuffer.from[TableId](originalTableIds.sortBy(TableIdLabel.toLabel))
  private val list = new ListView[TableId](buffer) {
    cellFactory = (cell, value) => cell.text.value = TableIdLabel.toLabel(value)
  }

  def addTableNames(tableIds : TableIds) : Unit = {
    buffer.clear()
    buffer ++= tableIds.tableIds
  }

  def onTableSelected(useTable : TableId => Unit) : Unit =
      JFXUtil.onAction(list, { (selectedTable : TableId, _) =>
        useTable(selectedTable)
        })

  def control : Parent = list
}

