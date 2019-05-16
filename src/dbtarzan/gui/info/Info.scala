package dbtarzan.gui.info

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.geometry.Orientation

import dbtarzan.gui.info.ColumnsTable

/* splitter that seåareater table, details and potentially reoDetailsView  */
class Info(columnsTable: ColumnsTable, queryInfo : QueryInfo) {
  private val center = buildCenter()

  def control : SplitPane =  center

   /* builds the split panel containing the table and the foreign keys list */
  private def buildCenter() = new SplitPane {
    items ++= List(columnsTable.control, queryInfo.control)
    orientation() =  Orientation.VERTICAL
    maxHeight = Double.MaxValue    
    maxWidth = Double.MaxValue
    dividerPositions = 0.8
    SplitPane.setResizableWithParent(queryInfo.control, false)
  }
}