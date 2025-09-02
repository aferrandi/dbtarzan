package dbtarzan.gui.tabletabs

import dbtarzan.messages._
import scalafx.Includes._
import scalafx.scene.control.Tab

import scala.collection.mutable

trait TTableForMapWithId {
  def getId : QueryId
}

case class TTableWithTab[TBL <: TTableForMapWithId](table : TBL, tab : Tab)

/* One tab for each table */
class TableTabsMap[TBL <: TTableForMapWithId] {
  private val mapTable = mutable.HashMap.empty[QueryId, TTableWithTab[TBL]]

  def idsFromTabs(toCloseTabs : List[javafx.scene.control.Tab]) : List[QueryId] = 
    mapTable.filter({ case (_, tableAndTab) => toCloseTabs.contains(tableAndTab.tab.delegate)}).keys.toList
 
  def addBrowsingTable(browsingTable : TBL, tab : javafx.scene.control.Tab) : Unit =
    mapTable += browsingTable.getId -> TTableWithTab[TBL](browsingTable, tab)

  def withQueryId(id : QueryId, doWith : TTableWithTab[TBL] => Unit) : Unit =
    mapTable.get(id).foreach(tableAndTab => doWith(tableAndTab))

  def withQueryIdForce(id : QueryId, doWith : TTableWithTab[TBL] => Unit, create: => TTableWithTab[TBL]) : Unit = {
    val tableAndTab  = mapTable.getOrElseUpdate(id, create) 
    doWith(tableAndTab)
  }

  def tableWithQueryId(id : QueryId, doWith : TBL => Unit) : Unit =
    withQueryId(id , table => doWith(table.table))

  def tabsWithIds(ids : List[QueryId]) : List[javafx.scene.control.Tab] = 
    mapTable.view.filterKeys(id => ids.contains(id)).values.map(_.tab.delegate).toList

  def removeTablesWithIds(ids : List[QueryId]) : Unit =
    mapTable --= ids

  def tableIdForTab(tab : Tab) : Option[QueryId] = 
    mapTable.values.find(_.tab == tab).map(_.table.getId)   
}

