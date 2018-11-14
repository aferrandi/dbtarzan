package dbtarzan.gui

import scalafx.scene.control.Tab
import scalafx.Includes._
import scala.collection.mutable.HashMap
import dbtarzan.messages._

case class BrowsingTableWIthTab(table : BrowsingTable, tab : Tab)

/* One tab for each table */
class TableTabsMap() {
  private val mapTable = HashMap.empty[QueryId, BrowsingTableWIthTab]

  def idsFromTabs(toCloseTabs : List[javafx.scene.control.Tab]) : List[QueryId] = 
    mapTable.filter({ case (id, tableAndTab) => toCloseTabs.contains(tableAndTab.tab.delegate)}).keys.toList
 
  def addBrowsingTable(browsingTable : BrowsingTable, tab : javafx.scene.control.Tab) : Unit = 
    mapTable += browsingTable.getId -> BrowsingTableWIthTab(browsingTable, tab)

  def withQueryId(id : QueryId, doWith : BrowsingTableWIthTab => Unit) : Unit =
    mapTable.get(id).foreach(tableAndTab => doWith(tableAndTab))

  def tableWithQueryId(id : QueryId, doWith : BrowsingTable => Unit) : Unit = 
    withQueryId(id , table => doWith(table.table))
     
  def tabsWithIds(ids : List[QueryId]) : List[javafx.scene.control.Tab] = 
    mapTable.filterKeys(id => ids.contains(id)).values.map(_.tab.delegate).toList

  def removeTablesWithIds(ids : List[QueryId]) : Unit =
      mapTable --= ids

  def tableIdForTab(tab : Tab) : Option[QueryId] = 
    mapTable.values.find(_.tab == tab).map(_.table.getId)   
}

