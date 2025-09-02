package dbtarzan.gui.tabletabs

import dbtarzan.messages.{OriginalQuery, QueryId}

import scala.collection.mutable

class TabsToClose {
  private val tabsToClose = mutable.HashMap.empty[QueryId, OriginalQuery]

  def addToCloseWhenNewTabOpens(newId: QueryId, originalQuery: Option[OriginalQuery]): Unit = {
    originalQuery.foreach(original => { tabsToClose += newId -> original })
  }

  def removeAndGetToCloseWhenNewTabOpens(newId: QueryId): Option[OriginalQuery] = {
    tabsToClose.remove(newId)
  }
}
