package dbtarzan.gui

import scalafx.scene.control.{ SplitPane, ContextMenu, MenuItem, Label }
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
  /* when the database connection closes itself (e.g. Azure SQL server) this resets it */
  private val menuReset = new MenuItem("Reset database connection") {
      onAction = {e: ActionEvent => dbActor ! QueryReset(databaseName)}
  }
  private val tableList = new TableList()
  private val id = IDGenerator.databaseId(databaseName)
  private val tableTabs = new TableTabs(dbActor, guiActor, id)  
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(id, tableName))
  private val pane = new SplitPane {
    val tableListWithTitle = new BorderPane {
      top = new Label("Tables") {
        margin = Insets(5)
        contextMenu = new ContextMenu(menuReset)   
      }
      center = tableList.control
    }
    items.addAll(tableListWithTitle, tableTabs.control)
    dividerPositions = 0.20
    SplitPane.setResizableWithParent(tableListWithTitle, false)
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