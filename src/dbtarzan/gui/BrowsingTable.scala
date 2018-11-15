package dbtarzan.gui

import scalafx.stage.Stage
import scalafx.scene.control.{MenuItem, Menu, MenuBar }
import scalafx.scene.layout.BorderPane
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ DBTable, ForeignKey, Field, Filter, FollowKey, OrderByField, OrderByFields, OrderByDirection, DatabaseId, TableId }
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.orderby.OrderByEditorStarter
import dbtarzan.messages._

/* table + constraint input box + foreign keys */
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, dbTable : DBTable, databaseId : DatabaseId) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val foreignKeyList = new ForeignKeyList()
  private val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, "Foreign keys") 
  private val queryId = IDGenerator.queryId(TableId(databaseId, dbTable.tableDescription.name))
  private val table = new Table(dbActor, guiActor, queryId, dbTable)
  private val splitter = new BrowsingTableSplitter(table, foreignKeyListWithTitle)
  private var useNewTable : DBTable => Unit = table => {}
  private var rowDetailsView : Option[RowDetailsView] = None
  table.setRowClickListener(row => rowDetailsView.foreach(details => details.displayRow(row)))
  private val queryText = new QueryText() { 
    onEnter(text => {
        val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
        useNewTable(tableWithFilters)
    })
  }
  splitter.fillSplitPanel(rowDetailsView)
  private val progressBar = new TableProgressBar()
  private val layout = new BorderPane {
    top = buildTop()
    center = splitter.splitCenter
    bottom = progressBar.control
  }
  foreignKeyList.onForeignKeySelected(key => openTableConnectedByForeignKey(key))


  def orderByField(field : Field) : Unit = 
    useNewTable(dbTable.withOrderByFields(
      OrderByFields(List(OrderByField(field, OrderByDirection.ASC))
      )))

  def startOrderByEditor() : Unit = {
    OrderByEditorStarter.openOrderByEditor(stage(), dbTable, useNewTable)
  }

  private def stage() : Stage = 
    new Stage(layout.scene.window().asInstanceOf[javafx.stage.Stage])

  private def buildOrderByMenu() = new Menu("Order by") {
      items = dbTable.columnNames.map(f => 
        new MenuItem(f.name) {
            onAction = { e: ActionEvent => guiActor ! RequestOrderByField(queryId, f) }
        }) :+ new MenuItem("More...") {
            onAction = { e: ActionEvent => guiActor ! RequestOrderByEditor(queryId) }
        }
    }

  private def openTableConnectedByForeignKey(key : ForeignKey) : Unit = {
      println("Selected "+key)
      val checkedRows = table.getCheckedRows
      val foreignTableId = TableId(databaseId, key.to.table)
      if(!checkedRows.isEmpty) {
        dbActor ! QueryColumnsFollow(foreignTableId, FollowKey(dbTable.columnNames, key, checkedRows))
      } else {
        dbActor ! QueryColumns(foreignTableId)
        log.warning("No rows selected with key "+key.name+". Open table "+key.to.table+" without filter.")
      }
  } 

  private def buildTop() : BorderPane = new BorderPane {        
      stylesheets += "orderByMenuBar.css"
      left = TableMenu.buildMainMenu(guiActor, queryId)
	    center = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
	    right =new MenuBar {
		    menus = List(buildOrderByMenu())
        stylesheets += "orderByMenuBar.css"
      }
	}
              
  def switchRowDetailsView() : Unit = {
    rowDetailsView = rowDetailsView match {
      case None => Some(new RowDetailsView(dbTable, table.firstSelectedRow()))
      case Some(_) => None
    }
    splitter.fillSplitPanel(rowDetailsView)  
  }

  /* if someone entere a query in the text box on the top of the table it creates a new table that depends by this query */
  def onNewTable(useTable : DBTable => Unit) : Unit = {
    useNewTable = useTable
  }

  /* adds the following rows to the table */
  def addRows(rows : ResponseRows) : Unit  = { 
    table.addRows(rows.rows)
    progressBar.receivedRows()
  }

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
  }

  def copySelectionToClipboard(includeHeaders : Boolean) : Unit = 
    table.copySelectionToClipboard(includeHeaders) 

  def copySQLToClipboard() : Unit = try {
    JFXUtil.copyTextToClipboard(dbTable.sql.sql)
    log.info("SQL copied")
  } catch {
    case ex : Exception => log.error("Copying SQL to the clipboard got ", ex)
  }

  def checkAllTableRows() : Unit = 
    table.checkAll(true) 

  def checkNoTableRows() : Unit = 
    table.checkAll(false) 
    
  def getId : QueryId = queryId

  def rowsNumber = table.rowsNumber

  def sql = dbTable.sql
  
  def control : Parent = layout
}