package dbtarzan.gui

import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.gui.browsingtable._
import dbtarzan.gui.info.{ColumnsTable, IndexesInfo, Info, QueryInfo}
import dbtarzan.gui.orderby.OrderByEditorStarter
import dbtarzan.gui.tabletabs.TTableForMapWithId
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/* table + constraint input box + foreign keys */
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, structure : DBTableStructure, queryId : QueryId, localization: Localization)
  extends TControlBuilder with TTableForMapWithId {
  private val log = new Logger(guiActor)
  private val foreignKeyList = new ForeignKeyList(log)
  private val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, localization.foreignKeys) 
  private val columnsTable = new ColumnsTable(structure.columns, guiActor, localization)
  private val queryInfo = new QueryInfo(SqlBuilder.buildSql(structure), localization)
  private val indexInfo = new IndexesInfo(guiActor, localization)
  private val info = new Info(columnsTable, queryInfo, indexInfo, localization, () => {
    dbActor ! QueryIndexes(queryId)
  })
  private val dbTable = new DBTable(structure)
  private val table = new Table(dbActor, guiActor, queryId, dbTable, localization)
  private val foreignKeysInfoSplitter = new ForeignKeysInfoSplitter(foreignKeyListWithTitle, info)
  private val splitter = new BrowsingTableSplitter(table, foreignKeysInfoSplitter)
  private var useNewTable : (DBTableStructure, Boolean) => Unit = (table, closeCurrentTab) => {}
  private var rowDetailsView : Option[RowDetailsView] = None
  private val rowDetailsApplicant = new RowDetailsApplicant(structure)
  private val queryText = new QueryText(structure.columns) {
    onEnter((text, closeCurrentTab) => {
      val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
      useNewTable(tableWithFilters, closeCurrentTab)
    })
  }
  table.setRowClickListener(row => openRowDisplay(row))
  table.setRowDoubleClickListener(row => {
    switchRowDetailsView()
    openRowDisplay(row)
  })

  splitter.splitPanelWithoutRowDetailsView()
  private val progressBar = new TableProgressBar(removeProgressBar)
  private val layout = new BorderPane {
    top = buildTop()
    center = splitter.control
    bottom = progressBar.control
  }
  foreignKeyList.onForeignKeySelected(openTableConnectedByForeignKey)

  private def openRowDisplay(row: Row): Unit = {
    rowDetailsView.foreach(details => {
      if (noMaxFieldSize())
        details.displayRow(row)
      else
        requestRowToDisplayInDetailsView(row, details)
    })
  }

  private def requestRowToDisplayInDetailsView(row: Row, details: RowDetailsView): Unit = {
    val query = rowDetailsApplicant.buildRowQueryFromRow(row)
    query match {
      case Some(rowStructure) => dbActor ! QueryOneRow(queryId, rowStructure)
      case None => {
        log.warning(localization.warningNoPrimaryKeyInTable(structure.description.name))
        details.displayRow(row)
      }
    }
  }

  private def noMaxFieldSize(): Boolean =
    structure.attributes.maxFieldSize.isEmpty

  def orderByField(field : Field) : Unit = {
    val orderByFields = OrderByFields(List(OrderByField(field, OrderByDirection.ASC)))
    val newStructure = dbTable.withOrderByFields(orderByFields)
    useNewTable(newStructure, false)
  }

  def startOrderByEditor() : Unit = {
    OrderByEditorStarter.openOrderByEditor(stage(), dbTable, useNewTable, localization)
  }

  private def removeProgressBar() : Unit = 
    layout.bottom = null

  private def stage() : Stage = 
    new Stage(layout.scene.window().asInstanceOf[javafx.stage.Stage])

  private def buildOrderByMenu() = new Menu(localization.orderBy) {
      items = dbTable.fields.map(f =>
        new MenuItem(f.name) {
            onAction = { e: ActionEvent => guiActor ! RequestOrderByField(queryId, f) }
        }) :+ new MenuItem(localization.more) {
            onAction = { e: ActionEvent => guiActor ! RequestOrderByEditor(queryId) }
        }
    }

  private def openTableConnectedByForeignKey(key : ForeignKey, closeCurrentTab : Boolean) : Unit = {
      log.debug("Selected "+key)
      if(closeCurrentTab)
        guiActor ! RequestRemovalThisTab(queryId) 
      val checkedRows = table.getCheckedRows
      val foreignTableId = key.to.table
      if(checkedRows.nonEmpty) {
        dbActor ! QueryColumnsFollow(foreignTableId, FollowKey(dbTable.fields, key, checkedRows))
      } else {
        dbActor ! QueryColumns(foreignTableId)
        log.warning(localization.noRowsFromForeignKey(key.name, key.to.table.tableName))
      }
  } 

  private def buildTop() : BorderPane = new BorderPane {        
    stylesheets += "orderByMenuBar.css"
    left = TableMenu.buildMainMenu(guiActor, queryId, localization)
    center = JFXUtil.withLeftTitle(queryText.textBox, localization.where+":")
    right =new MenuBar {
      menus = List(buildOrderByMenu())
      stylesheets += "orderByMenuBar.css"
    }
	}
              
  def switchRowDetailsView() : Unit = {
    rowDetailsView match {
      case None =>
        showRowDetailsView()
      case Some(_) => {
        splitter.splitPanelWithoutRowDetailsView()
        rowDetailsView = None
      }
    }
  }

  private def showRowDetailsView(): Unit = {
    table.selectOneIfNoneSelected()
    table.firstSelectedRow().foreach(row => {
      val view = new RowDetailsView(dbTable)
      splitter.splitPanelWithRowDetailsView(view)
      rowDetailsView = Some(view)
      openRowDisplay(row)
    })
  }

  /* if someone enters a query in the text box on the top of the table it creates a new table that depends by this query */
  def onNewTable(useTable : (DBTableStructure, Boolean) => Unit) : Unit = {
    useNewTable = useTable
  }

  /* adds the following rows to the table */
  def addRows(rows : ResponseRows) : Unit  = { 
    table.addRows(rows.rows)
    progressBar.receivedRows()
  }

  def addOneRow(oneRow: ResponseOneRow): Unit =
    rowDetailsView.foreach(details => details.displayRow(oneRow.row))

  def rowsError(ex : Exception) : Unit = queryText.showError()

  /* adds the foreign keys to the foreign key list */
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = {
    foreignKeyList.addForeignKeys(keys.keys)
    table.addForeignKeys(keys.keys)
    progressBar.receivedForeignKeys()
  }

  /* adds the foreign keys to the foreign key list */
  def addPrimaryKeys(keys : ResponsePrimaryKeys) : Unit = {
    table.addPrimaryKeys(keys.keys)
    progressBar.receivedPrimaryKeys()
    rowDetailsApplicant.addPrimaryKeys(keys.keys)
  }

  def addIndexes(indexes: ResponseIndexes): Unit =
    indexInfo.addRows(indexes.indexes.indexes)

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit = 
    table.copySelectionToClipboard(includeHeaders) 

  def checkAllTableRows() : Unit = 
    table.checkAll(true) 

  def checkNoTableRows() : Unit = 
    table.checkAll(false) 
    
  def getId : QueryId = queryId

  def rowsNumber: Int = table.rowsNumber

  def control : Parent = layout
}