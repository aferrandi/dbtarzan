package dbtarzan.gui

import scalafx.stage.Stage
import scalafx.scene.control.{SplitPane, MenuItem, Menu, MenuBar, CheckMenuItem }
import scalafx.scene.layout.BorderPane
import scalafx.scene.input.KeyCombination
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ ForeignKey, Filter, FollowKey, OrderByField, OrderByFields, OrderByDirection }
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.orderby.OrderByEditorStarter
import dbtarzan.messages._


/**
  table + constraint input box + foreign keys
*/
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, dbTable : dbtarzan.db.Table, databaseId : DatabaseId) extends TTable with TControlBuilder {
  private val foreignKeyList = new ForeignKeyList()
  private val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, "Foreign keys") 
  private val tableId = IDGenerator.tableId(databaseId, dbTable.tableDescription.name)
  private val table = new Table(dbActor, guiActor, tableId, dbTable)
  private var useNewTable : dbtarzan.db.Table => Unit = table => {}
  private var rowDetails : Option[RowDetailsView] = None
  table.setRowClickListener(row => rowDetails.foreach(details => details.displayRow(row)))
  private val log = new Logger(guiActor)
  private val queryText = new QueryText() { 
    onEnter(text => {
        val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
        useNewTable(tableWithFilters)
    })
  }
  private val splitCenter = buildCenter()
  fillSplitPanel()
  private val progressBar = new TableProgressBar()
  private val layout = new BorderPane {
    top = buildTop()
    center = splitCenter
    bottom = progressBar.control
  }
  foreignKeyList.onForeignKeySelected(key => openTableConnectedByForeignKey(key))

  private def openTableConnectedByForeignKey(key : ForeignKey) : Unit = {
      println("Selected "+key)
      val checkedRows = table.getCheckedRows
      if(!checkedRows.isEmpty) {
        dbActor ! QueryColumnsFollow(databaseId, key.to.table, FollowKey(dbTable.columnNames, key, checkedRows))
      } else {
        dbActor ! QueryColumns(databaseId, key.to.table)
        log.warning("No rows selected with key "+key.name+". Open table "+key.to.table+" without filter.")
      }
  } 

  private def buildTop() : BorderPane = new BorderPane {        
      stylesheets += "orderByMenuBar.css"
      left = buildMainMenu()
	    center = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
	    right =new MenuBar {
		    menus = List(buildOrderByMenu())
        stylesheets += "orderByMenuBar.css"
      }
	}

  private def buildMainMenu() = new MenuBar {
    menus = List(
      new Menu(JFXUtil.threeLines) {
        items = List(
            new MenuItem("Copy SQL To Clipboard") {
              onAction = (ev: ActionEvent) =>  try {
                JFXUtil.copyTextToClipboard(dbTable.sql)
                log.info("SQL copied")
              } catch {
                case ex : Exception => log.error("Copying SQL to the clipboard got ", ex)
              }
            },
            new MenuItem("Close tabs after this") {
                onAction = (ev: ActionEvent) => guiActor ! RequestRemovalTabsAfter(databaseId, tableId)
                accelerator = KeyCombination("Ctrl+ALT+A")
            },
            new MenuItem("Close tabs before this") {
              onAction = (ev: ActionEvent) => guiActor ! RequestRemovalTabsBefore(databaseId, tableId)
                accelerator = KeyCombination("Ctrl+ALT+B")
            },
            new MenuItem("Close all tabs") {                 
              onAction = (ev: ActionEvent) => guiActor ! RequestRemovalAllTabs(databaseId)
            },
            new MenuItem("Check All")  { 
              onAction = { ev: ActionEvent => table.checkAll(true) }
              accelerator = KeyCombination("Ctrl+SHIFT+A") 
              },
            new MenuItem("Uncheck All") { 
              onAction = { ev: ActionEvent => table.checkAll(false) } 
              accelerator = KeyCombination("Ctrl+SHIFT+N") 
              },
            new CheckMenuItem("Row Details") { 
              onAction = { ev: ActionEvent => switchRowDetails() }
              accelerator = KeyCombination("Ctrl+R")
              }
          )
        }
      )
      stylesheets += "orderByMenuBar.css"
    }

  private def switchRowDetails() : Unit = {
    rowDetails= rowDetails match {
      case None => Some(new RowDetailsView(dbTable, table.firstSelectedRow()))
      case Some(_) => None
    }
    fillSplitPanel()  
  }

  private def setSplitCenterItems(items : List[javafx.scene.Node]) : Unit = {
    splitCenter.items.clear()
    splitCenter.items ++= items
  }

  private def fillSplitPanel() : Unit = rowDetails match { 
    case Some(details) => {
        setSplitCenterItems(List(table.control, details.control, foreignKeyListWithTitle))
        splitCenter.dividerPositions_=(0.6, 0.8)      
    }
    case None => {
        setSplitCenterItems(List(table.control,  foreignKeyListWithTitle))
        splitCenter.dividerPositions = 0.8            
    }
  }

  private def buildOrderByMenu() = new Menu("Order by") {
      items = dbTable.columnNames.map(f => 
        new MenuItem(f.name) {
              onAction = {
                e: ActionEvent => useNewTable(dbTable.withOrderByFields(
                  OrderByFields(List(OrderByField(f, OrderByDirection.ASC))
                  )))
              }
        }) :+ new MenuItem("More...") {
              onAction = {                
                e: ActionEvent => {
                    var stage = new Stage(layout.scene.window().asInstanceOf[javafx.stage.Stage])
                    OrderByEditorStarter.openOrderByEditor(stage, dbTable, useNewTable)
                  }
              }
        }
    }

  /* builds the split panel containing the table and the foreign keys list */
  private def buildCenter() = new SplitPane {
    maxHeight = Double.MaxValue
    maxWidth = Double.MaxValue
    dividerPositions = 0.8
    SplitPane.setResizableWithParent(foreignKeyListWithTitle, false)
  }

  /* if someone entere a query in the text box on the top of the table it creates a new table that depends by this query */
  def onNewTable(useTable : dbtarzan.db.Table => Unit) : Unit = {
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


  def copySelectionToClipboard(includeHeaders : Boolean) : Unit = table.copySelectionToClipboard(includeHeaders) 


  def getId : TableId = tableId
  def rowsNumber = table.rowsNumber
  def sql = dbTable.sql
  def control : Parent = layout
}