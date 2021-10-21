package dbtarzan.gui.info

import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control._
import scalafx.scene.layout.BorderPane
import scalafx.Includes._

/* the info box on the right bottom, which shows the information views as tabs  */
class Info(columnsTable: ColumnsTable, queryInfo : QueryInfo, indexesInfo: IndexesInfo, localization : Localization,
           requestIndexInfo : () => Unit) {
  private val tabs = buildTabs()

  def control : Parent =  tabs

   /* builds the tab panel with the information views */
  private def buildTabs() = new TabPane {
    private val columnsTab: Tab = new Tab() {
      text = localization.columnsDescription
      content = new BorderPane {
        top = buildMenu()
        center = columnsTable.control
      }
    }
    private val queryInfoTab: Tab = new Tab() {
      text = localization.queryText
      content = queryInfo.control
    }
    private val indexesTab: Tab = new Tab() {
      text = localization.indexes
      onSelectionChanged = () => {
        if (indexesTab.selected() && !indexesInfo.complete())
          requestIndexInfo()
      }
      content = new BorderPane {
        top = buildMenu()
        center = indexesInfo.control
      }
    }
    tabs = List(
      queryInfoTab,
      columnsTab,
      indexesTab
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