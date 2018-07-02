package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, ContextMenu, MenuBar, Menu, MenuItem, Label, Button }
import scalafx.scene.layout.BorderPane
import scalafx.scene.Parent
import dbtarzan.db.{ ForeignKeyMapper, TableDescription, TableNames, Fields, IdentifierDelimiters }
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.messages._
import scalafx.geometry.Insets
import scalafx.event.ActionEvent

/**
  A panel containing all the tabs related to a database
*/
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseName : String) extends TControlBuilder {
  private val tableList = new TableList()
  private val id = IDGenerator.databaseId(databaseName)
  private val tableTabs = new TableTabs(dbActor, guiActor, id)  
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(id, tableName))
  private val pane = new SplitPane {
    val tableListWithTitle = new BorderPane {
      top = buildMenu()
      center = tableList.control
    }
    items.addAll(tableListWithTitle, tableTabs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, false)
  }

	private def buildMenu() = new MenuBar {
		menus = List(
		  new Menu("Tables") {
		    items = List(
		      new MenuItem("Connection Reset") {
		        onAction = {
		          e: ActionEvent => dbActor ! QueryReset(databaseName)
		        }
		      }
		    )
		  }
    )
  }

  def getDatabaseName = databaseName
  
  def addTableNames(names : TableNames) : Unit = 
    tableList.addTableNames(names)    
  
  def removeTables(ids : List[TableId]) : Unit = 
    tableTabs.removeTables(ids)

  def control : Parent = pane

  def addRows(rows : ResponseRows) : Unit = tableTabs.addRows(rows)

  /* received some foreign keys, that have to be shown within a table tab  */
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = tableTabs.addForeignKeys(keys) 

  /* received the columns of a table, that are used to build the table in a tab  */
  def addColumns(columns : ResponseColumns) : Unit= tableTabs.addColumns(columns)

  /* received the columns of a table, that are used to build the table coming from the selection of a foreign key, in a tab */
  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit= tableTabs.addColumnsFollow(columns)

  def getId = id

}