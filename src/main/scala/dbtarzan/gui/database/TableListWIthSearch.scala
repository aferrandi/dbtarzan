package dbtarzan.gui.database

import dbtarzan.db.{DatabaseId, TableId, TableIds}
import dbtarzan.gui.util.FilterText
import dbtarzan.localization.Localization
import dbtarzan.messages.QueryTablesByPattern
import org.apache.pekko.actor.ActorRef
import scalafx.scene.Parent
import scalafx.scene.layout.BorderPane

class TableListWIthSearch(dbActor : ActorRef, databaseId : DatabaseId, tableIds: List[TableId], localization : Localization) {
  private val tableList = new TableList(tableIds)
  private val filterText = new FilterText(dbActor ! QueryTablesByPattern(databaseId, _), localization)
  private val pane = new BorderPane {
    top = filterText.control
    center = tableList.control
  }

  def control : Parent = pane

  def addTableNames(tableIds: TableIds): Unit =
    tableList.addTableNames(tableIds)

  def onTableSelected(useTable: TableId => Unit): Unit =
    tableList.onTableSelected(useTable)
}
