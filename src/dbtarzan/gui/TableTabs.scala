package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip}
import scalafx.scene.Parent
import dbtarzan.messages._
import scala.collection.mutable.HashMap
import akka.actor.ActorRef
import dbtarzan.db.{DBTable, Fields, TableDescription, FollowKey, ForeignKeyMapper, QueryAttributesApplier, DatabaseId, TableId}
import scalafx.Includes._

case class BrowsingTableWIthTab(table : BrowsingTable, tab : Tab)

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tabs = new TabPane()
  private val mapTable = HashMap.empty[QueryId, BrowsingTableWIthTab]

  /* creates a table from scratch */
  private def createTable(tableId : TableId, columns : Fields, applier : QueryAttributesApplier) : DBTable = 
    DBTable.build(TableDescription(tableId.tableName, None, None), columns, applier)

  /* create a table that is given by following a foreign key of a table */  
  private def createTableFollow(columns : Fields, follow : FollowKey, applier : QueryAttributesApplier) : DBTable = {
    println("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns, applier) 
  }

  private def buildTab(dbTable : DBTable, browsingTable :  BrowsingTable) = new Tab() {      
      text = buildTabText(dbTable)
      content = browsingTable.control     
      tooltip.value = Tooltip("")
  }

  private def idsFromTabs(toCloseTabs : List[javafx.scene.control.Tab]) = 
        mapTable.filter({ case (id, tableAndTab) => toCloseTabs.contains(tableAndTab.tab.delegate)}).keys.toList

  private def requestRemovalTabsAfter(queryId : QueryId) : Unit = 
    withQueryId(queryId, table => {
        val toCloseTabs = tabs.tabs.reverse.takeWhile(_ != table.tab.delegate).toList // need to check the javafx class
        val toCloseIds = idsFromTabs(toCloseTabs)
        guiActor ! ResponseCloseTables(databaseId, toCloseIds)
    })

  private def requestRemovalTabsBefore(queryId : QueryId) : Unit =
    withQueryId(queryId, table => {
      val toCloseTabs = tabs.tabs.takeWhile(_ != table.tab.delegate).toList // need to check the javafx class
      val toCloseIds = idsFromTabs(toCloseTabs)
      guiActor ! ResponseCloseTables(databaseId, toCloseIds)
    })
 
  def requestRemovalAllTabs() : Unit = {
      val toCloseIds = idsFromTabs(tabs.tabs.toList)
      guiActor ! ResponseCloseTables(databaseId, toCloseIds)
  }   

  /*
    Normally it shows the name of the table.

    If the tab is derived from a foreign key, it is in the form:
      [table] < [origin table]
      where origin table is the table on the other side of the foreign key

    If a filter (where) has been applied, a star (*) character is shown at the end of the text 
  */
  private def buildTabText(dbTable : DBTable) : String = {
    def starForFilter(dbTable : DBTable) = if(dbTable.hasFilter) " *" else ""

    val description = dbTable.tableDescription
    description.name + description.origin.map("<"+_).getOrElse("") + starForFilter(dbTable)
  } 

  private def addBrowsingTable(dbTable : DBTable) : Unit = {
    val browsingTable = new BrowsingTable(dbActor, guiActor, dbTable, databaseId)
    val tab = buildTab(dbTable, browsingTable)
    tabs += tab
    tabs.selectionModel().select(tab)
    browsingTable.onNewTable(newTable => addBrowsingTable(newTable))
    mapTable += browsingTable.getId -> BrowsingTableWIthTab(browsingTable, tab)
  }

  private def withQueryId(id : QueryId, doWith : BrowsingTableWIthTab => Unit) : Unit =
    mapTable.get(id).foreach(tableAndTab => doWith(tableAndTab))

  private def addRows(rows : ResponseRows) : Unit = 
     withQueryId(rows.queryId, table => {
      table.table.addRows(rows)
      table.tab.tooltip.value.text = shortenIfTooLong(table.table.sql.sql, 500) +" ("+table.table.rowsNumber+" rows)"
    })

  def addColumns(columns : ResponseColumns) : Unit =  addBrowsingTable(createTable(columns.tableId, columns.columns, QueryAttributesApplier.from(columns.queryAttributes)))

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  addBrowsingTable(createTableFollow(columns.columns, columns.follow, QueryAttributesApplier.from(columns.queryAttributes)))
 
  def removeTables(ids : List[QueryId]) : Unit = {
      val tabsToClose = mapTable.filterKeys(id => ids.contains(id)).values.map(_.tab.delegate)
      mapTable --= ids
      tabs.tabs --= tabsToClose
    }

  def handleQueryIdMessage(msg: TWithQueryId) : Unit = msg match {
    case copy : CopySQLToClipboard => withQueryId(copy.queryId, table => table.table.copySQLToClipboard())
    case copy : CopySelectionToClipboard => withQueryId(copy.queryId, table => table.table.copySelectionToClipboard(copy.includeHeaders))
    case check : CheckAllTableRows => withQueryId(check.queryId, table => table.table.checkAllTableRows())
    case check :  CheckNoTableRows => withQueryId(check.queryId, table => table.table.checkNoTableRows())
    case keys : ResponsePrimaryKeys => withQueryId(keys.queryId, table => table.table.addPrimaryKeys(keys)) 
    case keys : ResponseForeignKeys => withQueryId(keys.queryId, table => table.table.addForeignKeys(keys))
    case switch: SwitchRowDetails => withQueryId(switch.queryId, table => table.table.switchRowDetails())
    case request : RequestRemovalTabsAfter => requestRemovalTabsAfter(request.queryId)
    case request : RequestRemovalTabsBefore => requestRemovalTabsBefore(request.queryId)
    case rows : ResponseRows => addRows(rows) 
    case _ => log.error("Table message "+msg+" not recognized")
  }    
  
  def control : Parent = tabs

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    mapTable.values.find(_.tab == currentTab).map(_.table.getId)   
  }

   private def shortenIfTooLong(text: String, maxLength : Int) : String =
    if(text.length <= maxLength)
      text
    else
      text.take(maxLength)+"..."
}

