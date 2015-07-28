package dbtarzan.gui

import scalafx.scene.control.SplitPane
import scalafx.scene.Parent
import dbtarzan.db.{ ForeignKeyMapper, TableDescription, TableNames, Fields }
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages._

/**
  A panel containing all the tabs related to a database
*/
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseName : String) extends TControlBuilder {
  private val tableList = new TableList()
  private val id = IDGenerator.databaseId(databaseName)
  private val tableTabs = new TableTabs(dbActor, guiActor, id)  
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(id, tableName))
  private val pane = new SplitPane {
        items.addAll(JFXUtil.withTitle(tableList.control, "Tables"), tableTabs.control)
        dividerPositions = 0.20
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