package dbtarzan.gui

import scalafx.scene.control.SplitPane
import dbtarzan.db.{ ForeignKeyMapper, TableDescription, TableNames, Fields }
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.{QueryColumns, QueryClose, IDGenerator, TableId}

/**
  A panel containing all the tabs related to a database
*/
class Database (dbActor : ActorRef, guiActor : ActorRef, databaseName : String){
  val tableList = new TableList()
  val id = IDGenerator.databaseId(databaseName)
  val tableTabs = new TableTabs(dbActor, guiActor, id)  
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(id, tableName))
  val pane = new SplitPane {
        items.addAll(JFXUtil.withTitle(tableList.list, "Tables"), tableTabs.tabs)
        dividerPositions = 0.20
  }
  def getDatabaseName = databaseName
  
  def addTableNames(names : TableNames) : Unit = 
    tableList.addTableNames(names)    
  
  def removeTables(ids : List[TableId]) : Unit = 
    tableTabs.removeTables(ids)
}