package dbtarzan.gui.info

import scalafx.scene.control.{ TabPane, Tab }
import scalafx.scene.Parent
import scalafx.Includes._

import dbtarzan.localization.Localization

/* the info box on the right bottom, which shows the information views as tabs  */
class Info(columnsTable: ColumnsTable, queryInfo : QueryInfo, localization : Localization) {
  private val tabs = buildTabs()

  def control : Parent =  tabs

   /* builds the tab panel with the information views */
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