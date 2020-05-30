package dbtarzan.gui.info

import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane
import scalafx.Includes._

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
        content = new BorderPane {
          top = buildMenu()
          center = columnsTable.control
        }
      }
    )
  }
  private def buildMenu() = new MenuBar {
    menus = List(
      new Menu(JFXUtil.threeLines) {
        items = List(
          new MenuItem(localization.copyContentToClipboard) {
            onAction = {
              e: ActionEvent => columnsTable.contentToClipboard()
            }
          })
        })
    stylesheets += "orderByMenuBar.css"
  }

}