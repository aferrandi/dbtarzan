package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.db._
import dbtarzan.gui.tabletabs.{BrowsingTableWithTab, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.messages._
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.scene.control.{Tab, TabPane, Tooltip}

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tabs = new TabPane()
  private val tables = new TableTabsMap()
  private val tablesToClose = new TabsToClose()

  /* creates a table from scratch */
  private def createTable(tableId : TableId, columns : Fields, attributes : QueryAttributes) : DBTableStructure = 
    DBTableStructure.build(TableDescription(tableId.tableName, None, None), columns, attributes)

  /* create a table that is given by following a foreign key of a table */  
  private def createTableFollow(columns : Fields, follow : FollowKey, attributes : QueryAttributes) : DBTableStructure = {
    log.debug("table follow created "+columns)
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

  private def addOneRow(table: BrowsingTable, oneRow : ResponseOneRow) : Unit =
    table.addOneRow(oneRow)

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
    tablesToClose.addToCloseWhenNewTabOpens(queryId, originalQuery)
    /* requests the foreign keys for this table. */
    dbActor ! QueryForeignKeys(queryId, structure)
    dbActor ! QueryRows(queryId, originalQuery, structure)
    /* requests the primary keys for this table. */
    dbActor ! QueryPrimaryKeys(queryId, structure)
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
    case keys : ResponsePrimaryKeys => createTabWith(keys.queryId, keys.structure, _.table.addPrimaryKeys(keys))
    case keys : ResponseForeignKeys => createTabWith(keys.queryId, keys.structure, _.table.addForeignKeys(keys))
    case indexes: ResponseIndexes =>  tables.tableWithQueryId(indexes.queryId, _.addIndexes(indexes))
    case switch: SwitchRowDetails => tables.tableWithQueryId(switch.queryId, _.switchRowDetailsView())
    case request : RequestRemovalTabsAfter => requestRemovalTabsAfter(request.queryId)
    case request : RequestRemovalTabsBefore => requestRemovalTabsBefore(request.queryId)
    case request : RequestRemovalThisTab => requestRemovalThisTab(request.queryId)
    case order : RequestOrderByField => tables.tableWithQueryId(order.queryId, _.orderByField(order.field))
    case order : RequestOrderByEditor => tables.tableWithQueryId(order.queryId, _.startOrderByEditor())
    case rows : ResponseRows => createTabWith(rows.queryId, rows.structure, addRows(_, rows))
    case errorRows : ErrorRows => tables.tableWithQueryId(errorRows.queryId, rowsError(_, errorRows))
    case oneRow : ResponseOneRow =>  tables.tableWithQueryId(oneRow.queryId, addOneRow(_, oneRow))
    case _ => log.error(localization.errorTableMessage(msg))
  }

  private def createTabWith(queryId: QueryId, structure : DBTableStructure, doWith : BrowsingTableWithTab => Unit): Unit = {
    tables.withQueryIdForce(queryId, doWith, buildBrowsingTable(queryId, structure))
    closeOriginalTabIfAny(queryId)
  }

  private def closeOriginalTabIfAny(queryId: QueryId): Unit = {
    tablesToClose.removeAndGetToCloseWhenNewTabOpens(queryId).foreach(original => {
      if (original.closeCurrentTab)
        guiActor ! RequestRemovalThisTab(original.queryId)
    })
  }

  def control : Parent = tabs

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    tables.tableIdForTab(currentTab)   
  }
}

