package dbtarzan.gui.tabletabs

import dbtarzan.gui.BrowsingTable
import dbtarzan.messages._
import scalafx.Includes._
import scalafx.scene.control.Tab

import scala.collection.mutable

case class BrowsingTableWithTab(table : BrowsingTable, tab : Tab)

/* One tab for each table */
class TableTabsMap() {
  private val mapTable = mutable.HashMap.empty[QueryId, BrowsingTableWithTab]

  def idsFromTabs(toCloseTabs : List[javafx.scene.control.Tab]) : List[QueryId] = 
    mapTable.filter({ case (id, tableAndTab) => toCloseTabs.contains(tableAndTab.tab.delegate)}).keys.toList
 
  def addBrowsingTable(browsingTable : BrowsingTable, tab : javafx.scene.control.Tab) : Unit = 
    mapTable += browsingTable.getId -> BrowsingTableWithTab(browsingTable, tab)

  def withQueryId(id : QueryId, doWith : BrowsingTableWithTab => Unit) : Unit =
    mapTable.get(id).foreach(tableAndTab => doWith(tableAndTab))

  def withQueryIdForce(id : QueryId, doWith : BrowsingTableWithTab => Unit, create: => BrowsingTableWithTab) : Unit = {
    val tableAndTab  = mapTable.getOrElseUpdate(id, create) 
    doWith(tableAndTab)
  }

  def tableWithQueryId(id : QueryId, doWith : BrowsingTable => Unit) : Unit = 
    withQueryId(id , table => doWith(table.table))

  def tabsWithIds(ids : List[QueryId]) : List[javafx.scene.control.Tab] = 
    mapTable.view.filterKeys(id => ids.contains(id)).values.map(_.tab.delegate).toList

  def removeTablesWithIds(ids : List[QueryId]) : Unit =
    mapTable --= ids

  def tableIdForTab(tab : Tab) : Option[QueryId] = 
    mapTable.values.find(_.tab == tab).map(_.table.getId)   
}

