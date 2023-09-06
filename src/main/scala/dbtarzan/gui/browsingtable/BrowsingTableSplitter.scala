package dbtarzan.gui.browsingtable

import scalafx.scene.control.SplitPane
import dbtarzan.gui.Table
import dbtarzan.gui.rowdetails.RowDetailsView

/* splitter that seåareater table, details and potentially reoDetailsView  */
class BrowsingTableSplitter(table : Table, foreignKeysAndInfo: ForeignKeysInfoSplitter) {
  private val center = buildCenter()

  def control : SplitPane =  center

  private def setSplitCenterItems(items : List[javafx.scene.Node]) : Unit = {
    center.items.clear()
    center.items ++= items
  }

  def splitPanelWithRowDetailsView(details: RowDetailsView): Unit = {
    setSplitCenterItems(List(table.control, details.control, foreignKeysAndInfo.control))
    center.dividerPositions_=(0.6, 0.8)
  }

  def splitPanelWithoutRowDetailsView(): Unit = {
    setSplitCenterItems(List(table.control, foreignKeysAndInfo.control))
    center.dividerPositions = 0.8
  }

  /* builds the split panel containing the table and the foreign keys list */
  private def buildCenter() = new SplitPane {
    maxHeight = Double.MaxValue
    maxWidth = Double.MaxValue
    dividerPositions = 0.8
    SplitPane.setResizableWithParent(foreignKeysAndInfo.control, false)
  }
}