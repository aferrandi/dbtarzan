package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.messages._
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.scene.control.{Tab, TabPane, Tooltip}

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, localization : Localization)
  extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val tabs = new TabPane()
  private val tables = new TableTabsMap[BrowsingTable]()
  private val tablesToClose = new TabsToClose()

  def addColumns(columns : ResponseColumns) : Unit =  {
    val structure = createTable(columns.tableId, columns.columns, columns.queryAttributes)
    queryTableContent(columns.tableId, None, structure)
  }

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  {
    val structure = createTableFollow(columns.columns, columns.follow, columns.queryAttributes)
    queryTableContent(columns.tableId, None, structure)
  }

  def removeTables(ids : List[QueryId]) : Unit = {
    val tabsToClose = tables.tabsWithIds(ids)
    tables.removeTablesWithIds(ids)
    tabs.tabs --= tabsToClose
  }

  def requestRemovalAllTabs() : Unit =
    removeTabs(tabs.tabs.toList)

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    tables.tableIdForTab(currentTab)
  }

  def control : Parent = tabs

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

  /* creates a table from scratch */
  private def createTable(tableId : TableId, columns : Fields, attributes : QueryAttributes) : DBTableStructure = 
    DBTableStructure.build(TableDescription(tableId.tableName, None, None), columns, attributes)

  /* create a table that is given by following a foreign key of a table */  
  private def createTableFollow(columns : Fields, follow : FollowKey, attributes : QueryAttributes) : DBTableStructure = {
    log.debug("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns, attributes) 
  }

  private def buildTab(dbTable : DBTableStructure, browsingTable :  BrowsingTable) = new Tab() {      
    text = TableStructureText.buildTabText(dbTable)
    content = browsingTable.control     
    tooltip.value = Tooltip("")
  }

  private def removeTabs(toCloseTabs : List[javafx.scene.control.Tab]) : Unit = {
    val toCloseIds = tables.idsFromTabs(toCloseTabs)
    toCloseIds.groupBy(toCloseId => toCloseId.tableId.databaseId)
      .foreach({case (databaseId, toCloseIdsWithDatabaseId) => guiActor ! ResponseCloseTables(databaseId, toCloseIdsWithDatabaseId)})
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

  private def buildBrowsingTable(queryId: QueryId, structure : DBTableStructure) : TTableWithTab[BrowsingTable] = {
    val browsingTable = new BrowsingTable(dbActor, guiActor, structure, queryId, localization)
    val tab = buildTab(structure, browsingTable)
    tabs += tab
    tabs.selectionModel().select(tab)
    
    browsingTable.onNewTable((newStructure, closeCurrentTab) => 
        queryTableContent(queryId.tableId ,Some(OriginalQuery(queryId, closeCurrentTab)), newStructure)
      )
    TTableWithTab[BrowsingTable](browsingTable, tab)
  }

  private def addRows(table: TTableWithTab[BrowsingTable], rows : ResponseRows) : Unit = {
    table.table.addRows(rows)
    table.tab.tooltip.value.text = table.table.rowsNumber+" rows"
  }

  private def addOneRow(table: BrowsingTable, oneRow : ResponseOneRow) : Unit =
    table.addOneRow(oneRow)

  private def rowsError(table: BrowsingTable, error: ErrorRows) : Unit = {
    table.rowsError(error.ex)
    log.error(localization.errorRequestingTheRows(error.queryId), error.ex)
  }

  private def queryTableContent(tableId: TableId, originalQuery : Option[OriginalQuery], structure : DBTableStructure) : Unit = {
    val queryId = IDGenerator.queryId(tableId)
    tablesToClose.addToCloseWhenNewTabOpens(queryId, originalQuery)
    // requests the foreign keys for this table.
    dbActor ! QueryForeignKeys(queryId, structure)
    // requests the rows of this table.
    dbActor ! QueryRows(queryId, originalQuery, structure)
    // requests the primary keys for this table.
    dbActor ! QueryPrimaryKeys(queryId, structure)
  }

  private def createTabWith(queryId: QueryId, structure : DBTableStructure, doWith : TTableWithTab[BrowsingTable] => Unit): Unit = {
    tables.withQueryIdForce(queryId, doWith, buildBrowsingTable(queryId, structure))
    closeOriginalTabIfAny(queryId)
  }

  private def closeOriginalTabIfAny(queryId: QueryId): Unit = {
    tablesToClose.removeAndGetToCloseWhenNewTabOpens(queryId).foreach(original => {
      if (original.closeCurrentTab)
        guiActor ! RequestRemovalThisTab(original.queryId)
    })
  }
}

