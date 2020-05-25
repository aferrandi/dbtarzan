package dbtarzan.gui.table

import dbtarzan.db.Field
import dbtarzan.gui.util.JFXUtil
import scalafx.collections.ObservableBuffer

class TableToClipboard(rows : ObservableBuffer[CheckedRow], names : List[Field]) {
  private def selectedRowsToString() : String = {
    rows.map(cellsInRow => cellsInRow.values.map(cell => cell()).mkString("\t") ).mkString("\n")
  }

  private def headersToString() : String =
    names.map(_.name).mkString("\t")

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit =
    if (includeHeaders)
      JFXUtil.copyTextToClipboard(headersToString() + "\n" + selectedRowsToString())
    else
      JFXUtil.copyTextToClipboard(selectedRowsToString())
}
