package dbtarzan.gui

import scalafx.stage.Stage
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.db.{DBTable, DBTableStructure, Field, Filter, FollowKey, ForeignKey, OrderByDirection, OrderByField, OrderByFields, Row, SqlBuilder, TableId}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.orderby.OrderByEditorStarter
import dbtarzan.gui.browsingtable.{BrowsingTableSplitter, ForeignKeysInfoSplitter, QueryText, RowDetailsApplicant, RowDetailsView, TableMenu, TableProgressBar}
import dbtarzan.gui.info.{ColumnsTable, Info, QueryInfo}
import dbtarzan.messages._
import dbtarzan.localization.Localization

/* table + constraint input box + foreign keys */
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, structure : DBTableStructure, queryId : QueryId, localization: Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val foreignKeyList = new ForeignKeyList(log)
  private val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, localization.foreignKeys) 
  private val columnsTable = new ColumnsTable(structure.columns, guiActor, localization)
  private val queryInfo = new QueryInfo(SqlBuilder.buildSql(structure), localization)
  private val info = new Info(columnsTable, queryInfo, localization)
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

  splitter.fillSplitPanel(rowDetailsView)
  private val progressBar = new TableProgressBar(removeProgressBar)
  private val layout = new BorderPane {
    top = buildTop()
    center = splitter.control
    bottom = progressBar.control
  }
  foreignKeyList.onForeignKeySelected(openTableConnectedByForeignKey)

  private def openRowDisplay(row: Row): Unit = {
    if(structure.attributes.maxFieldSize.isEmpty)
      displayRow(row)
    else {
      val query = rowDetailsApplicant.buildRowQueryFromRow(row)
      query match {
        case Some(rowStructure) => dbActor ! QueryOneRow(queryId, rowStructure)
        case None => {
          log.warning(localization.warningNoPrimaryKeyInTable(structure.description.name))
          displayRow(row)
        }
      }
    }
  }

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
      val foreignTableId = TableId(queryId.tableId.databaseId, key.to.table)
      if(checkedRows.nonEmpty) {
        dbActor ! QueryColumnsFollow(foreignTableId, FollowKey(dbTable.fields, key, checkedRows))
      } else {
        dbActor ! QueryColumns(foreignTableId)
        log.warning(localization.noRowsFromForeignKey(key.name, key.to.table))
      }
  } 

  private def buildTop() : BorderPane = new BorderPane {        
    stylesheets += "orderByMenuBar.css"
    left = TableMenu.buildMainMenu(guiActor, queryId, localization)
    print("Localization"+localization)
    center = JFXUtil.withLeftTitle(queryText.textBox, localization.where+":")
    right =new MenuBar {
      menus = List(buildOrderByMenu())
      stylesheets += "orderByMenuBar.css"
    }
	}
              
  def switchRowDetailsView() : Unit = {
    rowDetailsView = rowDetailsView match {
      case None => table.firstSelectedRow().map(row => {
          val view = new RowDetailsView(dbTable)
          openRowDisplay(row)
          view
        })
      case Some(_) => None
    }
    splitter.fillSplitPanel(rowDetailsView)
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
    displayRow(oneRow.row)

  private def displayRow(row: Row): Unit = {
    rowDetailsView.foreach(details => details.displayRow(row))
  }

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