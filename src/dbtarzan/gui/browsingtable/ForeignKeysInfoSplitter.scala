package dbtarzan.gui.browsingtable

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.geometry.Orientation

import dbtarzan.gui.info.Info

/* splitter that seåareater table, details and potentially reoDetailsView  */
class ForeignKeysInfoSplitter(foreignKeys : BorderPane, info: Info) {
  private val center = buildCenter()

  def control : SplitPane =  center

   /* builds the split panel containing the table and the foreign keys list */
  private def buildCenter() = new SplitPane {
    items ++= List(foreignKeys, info.control)
    orientation() =  Orientation.VERTICAL
    maxHeight = Double.MaxValue    
    maxWidth = Double.MaxValue
    dividerPositions = 0.6
    SplitPane.setResizableWithParent(info.control, false)
  }
}