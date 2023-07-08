package dbtarzan.gui.tabletabs

import dbtarzan.db._
import dbtarzan.messages.QueryId
import org.scalatest.flatspec.AnyFlatSpec
import scalafx.Includes._
import dbtarzan.testutil.TestDatabaseIds

case class SimpleTableForMap(getId : QueryId) extends TTableForMapWithId

class TableTabsMapTest extends AnyFlatSpec {
  val table1AId = QueryId(TestDatabaseIds.simpleTableId("table1"), "0x1232")
  val table2BId = QueryId(TestDatabaseIds.simpleTableId("table2"), "0x3232")
  val table3CId = QueryId(TestDatabaseIds.simpleTableId("table3"), "0x2243")
  val tab1A = new javafx.scene.control.Tab("table1A")
  val tab2B = new javafx.scene.control.Tab("table2B")
  val tab3C = new javafx.scene.control.Tab("table3C")

  "ids of the added tabs/tables" should "be found from their tabs" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table2BId), tab2B)
    assert(tableTabsMap.tableIdForTab(tab1A).get == table1AId)
    assert(tableTabsMap.tableIdForTab(tab2B).get == table2BId)
  }

  "tabs of the added tabs/tables" should "be found as list by ids" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table2BId), tab2B)
    assert(tableTabsMap.tabsWithIds(List(table1AId, table2BId)) == List(tab1A, tab2B))
    tableTabsMap.removeTablesWithIds(List(table1AId, table2BId))
    assert(tableTabsMap.tabsWithIds(List(table1AId, table2BId)) == List.empty)
  }

  "added tabs/tables" should "be found by ids" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table2BId), tab2B)
    var tableFound1: Option[TTableWithTab[SimpleTableForMap]] = None
    tableTabsMap.withQueryId(table1AId, t => tableFound1 = Some(t))
    assert(tableFound1.get.table.getId === table1AId)
    assert(tableFound1.get.tab === tab1A)
    var tableFound2: Option[TTableWithTab[SimpleTableForMap]] = None
    tableTabsMap.withQueryId(table2BId, t => tableFound2 = Some(t))
    assert(tableFound2.get.table.getId === table2BId)
    assert(tableFound2.get.tab === tab2B)
    var tableNotFound3: Option[TTableWithTab[SimpleTableForMap]] = None
    tableTabsMap.withQueryId(table3CId, t => tableNotFound3 = Some(t))
    assert(tableNotFound3 === None)
  }

  "table of added tabs/tables" should "be found by ids" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    var tableFound1: Option[SimpleTableForMap] = None
    tableTabsMap.tableWithQueryId(table1AId, t => tableFound1 = Some(t))
    assert(tableFound1.get.getId === table1AId)
    var tableFound2: Option[SimpleTableForMap] = None
    tableTabsMap.tableWithQueryId(table2BId, t => tableFound2 = Some(t))
    assert(tableFound2 === None)
  }

  "added tabs/tables" should "be found by ids and created if not" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    var tableFound1: Option[TTableWithTab[SimpleTableForMap]] = None
    tableTabsMap.withQueryIdForce(table1AId, t => tableFound1 = Some(t), TTableWithTab(SimpleTableForMap(table1AId), tab1A))
    assert(tableFound1.get.table.getId === table1AId)
    assert(tableFound1.get.tab === tab1A)
    var tableFound2: Option[TTableWithTab[SimpleTableForMap]] = None
    tableTabsMap.withQueryIdForce(table2BId, t => tableFound2 = Some(t), TTableWithTab(SimpleTableForMap(table2BId), tab2B))
    assert(tableFound2.get.table.getId === table2BId)
    assert(tableFound2.get.tab === tab2B)
  }


  "ids of the added tabs/table" should "be found as list by tabs" in {
    val tableTabsMap = new TableTabsMap[SimpleTableForMap]()
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table1AId), tab1A)
    tableTabsMap.addBrowsingTable(SimpleTableForMap(table2BId), tab2B)
    assert(tableTabsMap.idsFromTabs(List(tab1A, tab2B)) == List(table1AId, table2BId))
    tableTabsMap.removeTablesWithIds(List(table1AId, table2BId))
    assert(tableTabsMap.idsFromTabs(List(tab1A, tab2B)) == List.empty)
  }
}
