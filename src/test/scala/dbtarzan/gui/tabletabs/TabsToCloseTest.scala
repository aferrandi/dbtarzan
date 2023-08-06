package dbtarzan.gui.tabletabs

import dbtarzan.messages.{OriginalQuery, QueryId}
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec

class TabsToCloseTest extends AnyFlatSpec {
  val table1AId: QueryId = QueryId(TestDatabaseIds.simpleTableId("table1"), "0x1232")
  val table1BId: QueryId = QueryId(TestDatabaseIds.simpleTableId("table1"), "0x3232")

  "adding and removing from tabsToCloe" should "find the original element but not the second time" in {
    val tabsToClose = new TabsToClose()
    tabsToClose.addToCloseWhenNewTabOpens(table1AId, Some(OriginalQuery(table1BId, true)))
    val originalQuery = tabsToClose.removeAndGetToCloseWhenNewTabOpens(table1AId)
    assert(originalQuery.get === OriginalQuery(table1BId, true))
    val noneQuery = tabsToClose.removeAndGetToCloseWhenNewTabOpens(table1AId)
    assert(noneQuery === None)
  }

  "adding nothing to tabsToClose" should "find no original element" in {
    val tabsToClose = new TabsToClose()
    tabsToClose.addToCloseWhenNewTabOpens(table1AId, None)
    val noneQuery = tabsToClose.removeAndGetToCloseWhenNewTabOpens(table1AId)
    assert(noneQuery === None)
  }
}
