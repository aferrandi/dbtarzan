package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip}
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{DBTableStructure, Fields, TableDescription, FollowKey, QueryAttributes, DatabaseId, TableId}
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.messages._
import dbtarzan.gui.tabletabs.{ TableTabsMap, BrowsingTableWithTab }
import dbtarzan.localization.Localization

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tabs = new TabPane()
  private val tables = new TableTabsMap()

  /* creates a table from scratch */
  private def createTable(tableId : TableId, columns : Fields, attributes : QueryAttributes) : DBTableStructure = 
    DBTableStructure.build(TableDescription(tableId.tableName, None, None), columns, attributes)

  /* create a table that is given by following a foreign key of a table */  
  private def createTableFollow(columns : Fields, follow : FollowKey, attributes : QueryAttributes) : DBTableStructure = {
    println("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns, attributes) 
  }

  private def buildTab(dbTable : DBTableStructure, browsingTable :  BrowsingTable) = new Tab() {      
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

  private def requestRemovalThisTab(queryId : QueryId) : Unit =
    tables.withQueryId(queryId, table => removeTabs(List(table.tab)))

  def requestRemovalAllTabs() : Unit =
    removeTabs(tabs.tabs.toList)

  /*
    Normally it shows the name of the table.

    If the tab is derived from a foreign key, it is in the form:
      [table] < [origin table]
      where origin table is the table on the other side of the foreign key

    If a filter (where) has been applied, a star (*) character is shown at the end of the text 
  */
  private def buildTabText(structure : DBTableStructure) : String = {
    def starForFilter(structure : DBTableStructure) = if(DBTableStructure.hasFilter(structure)) " *" else ""

    val description = structure.description
    description.name + description.origin.map("<"+_).getOrElse("") + starForFilter(structure)
  } 

  private def buildBrowsingTable(queryId: QueryId, structure : DBTableStructure) : BrowsingTableWithTab = {
    val browsingTable = new BrowsingTable(dbActor, guiActor, structure, queryId, localization)
    val tab = buildTab(structure, browsingTable)
    tabs += tab
    tabs.selectionModel().select(tab)
    
    browsingTable.onNewTable((newStructure, closeCurrentTab) => 
        queryRows(Some(OriginalQuery(queryId, closeCurrentTab)), newStructure)
      )
    BrowsingTableWithTab(browsingTable, tab)
  }

  private def addRows(table: BrowsingTableWithTab, rows : ResponseRows) : Unit = {
    table.table.addRows(rows)
    table.tab.tooltip.value.text = table.table.rowsNumber+" rows"
  }

  private def rowsError(table: BrowsingTable, error: ErrorRows) : Unit = {
    table.rowsError(error.ex)
    log.error(localization.errorRequestingTheRows(error.queryId), error.ex)
  }

  def addColumns(columns : ResponseColumns) : Unit =  {
    val structure = createTable(columns.tableId, columns.columns, columns.queryAttributes)
    queryRows(None, structure) 
  }

  private def queryRows(originalQuery : Option[OriginalQuery], structure : DBTableStructure) : Unit = {
    val queryId = IDGenerator.queryId(TableId(databaseId, structure.description.name))
    dbActor ! QueryRows(queryId, originalQuery, structure)
  }

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  {
    val structure = createTableFollow(columns.columns, columns.follow, columns.queryAttributes)
    queryRows(None, structure) 
  }

  def removeTables(ids : List[QueryId]) : Unit = {
    val tabsToClose = tables.tabsWithIds(ids)
    tables.removeTablesWithIds(ids)
    tabs.tabs --= tabsToClose
  }

  def handleQueryIdMessage(msg: TWithQueryId) : Unit = msg match {
    case copy : CopySelectionToClipboard => tables.tableWithQueryId(copy.queryId, _.copySelectionToClipboard(copy.includeHeaders))
    case check : CheckAllTableRows => tables.tableWithQueryId(check.queryId, _.checkAllTableRows())
    case check : CheckNoTableRows => tables.tableWithQueryId(check.queryId, _.checkNoTableRows())
    case keys : ResponsePrimaryKeys => tables.tableWithQueryId(keys.queryId, _.addPrimaryKeys(keys)) 
    case keys : ResponseForeignKeys => tables.tableWithQueryId(keys.queryId, _.addForeignKeys(keys))
    case switch: SwitchRowDetails => tables.tableWithQueryId(switch.queryId, _.switchRowDetailsView())
    case request : RequestRemovalTabsAfter => requestRemovalTabsAfter(request.queryId)
    case request : RequestRemovalTabsBefore => requestRemovalTabsBefore(request.queryId)
    case request : RequestRemovalThisTab => requestRemovalThisTab(request.queryId)
    case order : RequestOrderByField => tables.tableWithQueryId(order.queryId, _.orderByField(order.field))
    case order : RequestOrderByEditor => tables.tableWithQueryId(order.queryId, _.startOrderByEditor())
    case rows : ResponseRows => { 
      tables.withQueryIdForce(rows.queryId, addRows(_, rows), buildBrowsingTable(rows.queryId, rows.structure))
      rows.original.foreach(original => {
            if(original.close)
              guiActor ! RequestRemovalThisTab(original.queryId)
        })
      }
    case errorRows : ErrorRows => tables.tableWithQueryId(errorRows.queryId, rowsError(_, errorRows))
    case _ => log.error(localization.errorTableMessage(msg))
  }    
  
  def control : Parent = tabs

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    tables.tableIdForTab(currentTab)   
  }
}

