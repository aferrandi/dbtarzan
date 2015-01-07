package dbtarzan.gui

import scalafx.scene.control.SplitPane
import dbtarzan.db.{ ForeignKeyMapper, TableDescription, TableNames, Fields }
import scalafx.Includes._
import akka.actor.ActorRef
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.{QueryColumns, IDGenerator}

/**
  A panel containing all the tabs related to a database
*/
class Database (dbActor : ActorRef, databaseName : String){
  val tableList = new TableList()
  val tableTabs = new TableTabs(dbActor, databaseName)
  val id = IDGenerator.databaseId(databaseName)
  tableList.onTableSelected(tableName => dbActor ! QueryColumns(id, tableName))
  val pane = new SplitPane {
        items.addAll(JFXUtil.withTitle(tableList.list, "Tables"), tableTabs.tabs)
        dividerPositions = 0.20
  }
  def getDatabaseName = databaseName
  def addTableNames(names : TableNames) : Unit = tableList.addTableNames(names)    
}