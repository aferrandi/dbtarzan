package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip}
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.db.{DBTable, Fields, TableDescription, FollowKey, ForeignKeyMapper, QueryAttributesApplier, DatabaseId, TableId}
import dbtarzan.messages._
import dbtarzan.gui.util.StringUtil
import dbtarzan.gui.tabletabs.TableTabsMap

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tabs = new TabPane()
  private val tables = new TableTabsMap()

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

  private def removeTabs(toCloseTabs : List[javafx.scene.control.Tab]) : Unit = {
    val toCloseIds = tables.idsFromTabs(toCloseTabs)
    guiActor ! ResponseCloseTables(databaseId, toCloseIds)
  }

  private def removeTabsBefore(queryId : QueryId, allTabsInOrder : List[javafx.scene.control.Tab]) : Unit = 
    tables.withQueryId(queryId, table => {
      val toCloseTabs = allTabsInOrder.takeWhile(_ != table.tab.delegate) // need to check the javafx class
      removeTabs(toCloseTabs)
    })

  private def requestRemovalTabsAfter(queryId : QueryId) : Unit =
    removeTabsBefore(queryId, tabs.tabs.reverse.toList) 

  private def requestRemovalTabsBefore(queryId : QueryId) : Unit =
    removeTabsBefore(queryId, tabs.tabs.toList) 

  def requestRemovalAllTabs() : Unit =
      removeTabs(tabs.tabs.toList)

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
    tables.addBrowsingTable(browsingTable, tab)
  }

  private def addRows(rows : ResponseRows) : Unit = 
     tables.withQueryId(rows.queryId, table => {
      table.table.addRows(rows)
      table.tab.tooltip.value.text = StringUtil.shortenIfTooLong(table.table.sql.sql, 500) +" ("+table.table.rowsNumber+" rows)"
    })

  def addColumns(columns : ResponseColumns) : Unit =  
    addBrowsingTable(createTable(columns.tableId, columns.columns, QueryAttributesApplier.from(columns.queryAttributes)))

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  
    addBrowsingTable(createTableFollow(columns.columns, columns.follow, QueryAttributesApplier.from(columns.queryAttributes)))
 
  def removeTables(ids : List[QueryId]) : Unit = {
    val tabsToClose = tables.tabsWithIds(ids)
    tables.removeTablesWithIds(ids)
    tabs.tabs --= tabsToClose
  }

  def handleQueryIdMessage(msg: TWithQueryId) : Unit = msg match {
    case copy : CopySQLToClipboard => tables.tableWithQueryId(copy.queryId, _.copySQLToClipboard())
    case copy : CopySelectionToClipboard => tables.tableWithQueryId(copy.queryId, _.copySelectionToClipboard(copy.includeHeaders))
    case check : CheckAllTableRows => tables.tableWithQueryId(check.queryId, _.checkAllTableRows())
    case check :  CheckNoTableRows => tables.tableWithQueryId(check.queryId, _.checkNoTableRows())
    case keys : ResponsePrimaryKeys => tables.tableWithQueryId(keys.queryId, _.addPrimaryKeys(keys)) 
    case keys : ResponseForeignKeys => tables.tableWithQueryId(keys.queryId, _.addForeignKeys(keys))
    case switch: SwitchRowDetails => tables.tableWithQueryId(switch.queryId, _.switchRowDetailsView())
    case request : RequestRemovalTabsAfter => requestRemovalTabsAfter(request.queryId)
    case request : RequestRemovalTabsBefore => requestRemovalTabsBefore(request.queryId)
    case order : RequestOrderByField => tables.tableWithQueryId(order.queryId, _.orderByField(order.field))
    case order : RequestOrderByEditor => tables.tableWithQueryId(order.queryId, _.startOrderByEditor())
    case rows : ResponseRows => addRows(rows) 
    case _ => log.error("Table message "+msg+" not recognized")
  }    
  
  def control : Parent = tabs

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    tables.tableIdForTab(currentTab)   
  }
}

