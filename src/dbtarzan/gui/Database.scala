package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, ContextMenu, MenuBar, Menu, MenuItem, Label, Button }
import scalafx.scene.layout.{ BorderPane, FlowPane }
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef
import scalafx.geometry.Insets
import scalafx.event.ActionEvent
import scalafx.scene.text.TextAlignment

import dbtarzan.messages._
import dbtarzan.gui.util.JFXUtil
import dbtarzan.db.{ ForeignKeyMapper, TableDescription, TableNames, Fields, IdentifierDelimiters }

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
      top = new FlowPane {
        children = List(buildMenu(), new Label("Tables"))
      }
      center = tableList.control
    }
    items.addAll(tableListWithTitle, tableTabs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, false)
  }

	private def buildMenu() = new MenuBar {
		menus = List(
		  new Menu(JFXUtil.threeLines) {
		    items = List(
		      new MenuItem("Connection Reset") {
		        onAction = {
		          e: ActionEvent => dbActor ! QueryReset(databaseName)
		        }
		      }
		    )
		  }
    )
    stylesheets += "orderByMenuBar.css"
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

  def requestRemovalTabsAfter(tableId : TableId) : Unit =  tableTabs.requestRemovalTabsAfter(tableId)

  def requestRemovalTabsBefore(tableId : TableId) : Unit = tableTabs.requestRemovalTabsBefore(tableId)
 
  def requestRemovalAllTabs() : Unit = tableTabs.requestRemovalAllTabs()


  def getId = id

}