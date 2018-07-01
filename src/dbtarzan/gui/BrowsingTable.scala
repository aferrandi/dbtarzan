package dbtarzan.gui

import scalafx.stage.Stage
import scalafx.scene.control.{ TableView, SplitPane, Button, MenuItem, Menu, MenuBar }
import scalafx.scene.layout.BorderPane
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import java.time.LocalDateTime

import dbtarzan.db.{ ForeignKey, ForeignKeyMapper, Filter, FollowKey, Fields, OrderByField, OrderByFields, OrderByDirection }
import dbtarzan.gui.util.JFXUtil
import dbtarzan.gui.orderby.OrderByEditorStarter
import dbtarzan.messages._


/**
  table + constraint input box + foreign keys
*/
class BrowsingTable(dbActor : ActorRef, guiActor : ActorRef, dbTable : dbtarzan.db.Table, databaseId : DatabaseId) extends TTable with TControlBuilder {
  private val foreignKeyList = new ForeignKeyList()
  private val id = IDGenerator.tableId(databaseId, dbTable.tableDescription.name)
  private val table = new Table(dbActor, id, dbTable)
  private var useNewTable : dbtarzan.db.Table => Unit = table => {}
  private val queryText = new QueryText() { 
    onEnter(text => {
        val tableWithFilters = dbTable.withAdditionalFilter(Filter(text))
        useNewTable(tableWithFilters)
    })
  }
  private val progressBar = new TableProgressBar()
  private val layout = new BorderPane {
    top = buildTop()
    center = buildCenter()
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
        guiActor ! Warning(LocalDateTime.now, "No rows selected with key "+key.name+". Open table "+key.to.table+" without filter.")
      }
  } 

  private def buildTop() : BorderPane = new BorderPane {
	    center = JFXUtil.withLeftTitle(queryText.textBox, "Where:")
	    right =new MenuBar {
		    menus = List(buildOrderByMenu())
        stylesheets += "orderByMenuBar.css"
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
        val foreignKeyListWithTitle = JFXUtil.withTitle(foreignKeyList.control, "Foreign keys") 
        items.addAll(table.control, foreignKeyListWithTitle)
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
    progressBar.receivedForeignKeys()
  }

  def getId = id
  def rowsNumber = table.rowsNumber
  def sql = dbTable.sql
  def control : Parent = layout
}