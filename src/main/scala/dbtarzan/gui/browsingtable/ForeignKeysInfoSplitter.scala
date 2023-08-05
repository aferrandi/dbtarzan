package dbtarzan.gui.browsingtable

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.geometry.Orientation

import dbtarzan.gui.info.Info

/* splitter with the foreign keys view on the top and the info view on the bottom  */
class ForeignKeysInfoSplitter(foreignKeys : BorderPane, info: Info) {
  private val center = buildCenter()

  def control : SplitPane =  center

   /* builds the split panel */
  private def buildCenter() = new SplitPane {
    items ++= List(foreignKeys, info.control)
    orientation() =  Orientation.Vertical
    maxHeight = Double.MaxValue    
    maxWidth = Double.MaxValue
    dividerPositions = 0.8
    SplitPane.setResizableWithParent(info.control, false)
  }
}