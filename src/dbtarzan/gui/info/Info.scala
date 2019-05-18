package dbtarzan.gui.info

import scalafx.scene.control.{ TabPane, Tab }
import scalafx.scene.Parent
import scalafx.Includes._

import dbtarzan.localization.Localization

/* splitter that seåareater table, details and potentially reoDetailsView  */
class Info(columnsTable: ColumnsTable, queryInfo : QueryInfo, localization : Localization) {
  private val tabs = buildTabs()

  def control : Parent =  tabs

   /* builds the split panel containing the table and the foreign keys list */
  private def buildTabs() = new TabPane {
    tabs = List(
      new Tab() {      
        text = localization.queryText
        content = queryInfo.control     
      },
      new Tab() {      
        text = localization.columnsDescription
        content = columnsTable.control     
      }
    )
  }
}