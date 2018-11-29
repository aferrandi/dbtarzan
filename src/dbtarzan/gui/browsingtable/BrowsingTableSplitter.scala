package dbtarzan.gui.browsingtable

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.Includes._

import dbtarzan.gui.Table

/* splitter that seåareater table, details and potentially reoDetailsView  */
class BrowsingTableSplitter(table : Table, foreignKeyListWithTitle: BorderPane) {
  private val center = buildCenter()

  def splitCenter : SplitPane =  center

  private def setSplitCenterItems(items : List[javafx.scene.Node]) : Unit = {
    center.items.clear()
    center.items ++= items
  }

  def fillSplitPanel(rowDetailsView : Option[RowDetailsView]) : Unit = rowDetailsView match { 
    case Some(details) => {
        setSplitCenterItems(List(table.control, details.control, foreignKeyListWithTitle))
        center.dividerPositions_=(0.6, 0.8)      
    }
    case None => {
        setSplitCenterItems(List(table.control,  foreignKeyListWithTitle))
        center.dividerPositions = 0.8            
    }
  }

   /* builds the split panel containing the table and the foreign keys list */
  private def buildCenter() = new SplitPane {
    maxHeight = Double.MaxValue
    maxWidth = Double.MaxValue
    dividerPositions = 0.8
    SplitPane.setResizableWithParent(foreignKeyListWithTitle, false)
  }
}