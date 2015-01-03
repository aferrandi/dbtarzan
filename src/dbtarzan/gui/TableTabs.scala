package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip}
import dbtarzan.messages._
import scala.collection.mutable.HashMap
import akka.actor.ActorRef
import dbtarzan.db.{Fields, TableDescription, FollowKey, ForeignKeyMapper}

/**
  One tab for each table
*/
class TableTabs(dbActor : ActorRef, databaseName : String) extends TTables {
  val tabs = new TabPane()
  val mapTable = HashMap.empty[TableId, BrowsingTable]

  private def createTable(tableName : String, columns : Fields) : dbtarzan.db.Table = 
    dbtarzan.db.Table.build(TableDescription(tableName, None, None), columns)

  private def createTableFollow(tableName : String, columns : Fields, follow : FollowKey) : dbtarzan.db.Table = {
    println("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns) 
  }


  private def buildTab(dbTable : dbtarzan.db.Table, browsingTable :  BrowsingTable) = new Tab() {
      val description = dbTable.tableDescription
      text = description.name + description.origin.map("<"+_).getOrElse("") + starForConstraint(dbTable)
      content = browsingTable.layout     
      tooltip.value = Tooltip(dbTable.sql) 
    }

  private def starForConstraint(dbTable : dbtarzan.db.Table) = 
    if(dbTable.hasConstraint) 
        " *" 
    else 
        ""

  def addBrowsingTable(dbTable : dbtarzan.db.Table) : Unit = {
    val browsingTable = new BrowsingTable(dbActor, dbTable, databaseName)
    val tab = buildTab(dbTable, browsingTable)
    tabs += tab
    tabs.selectionModel().select(tab)
    browsingTable.onTextEntered(newTable => addBrowsingTable(newTable))
    mapTable += browsingTable.id -> browsingTable
  }

  private def withTableId(id : TableId, doWith : BrowsingTable => Unit) : Unit =
    mapTable.get(id).foreach(table => doWith(table))

  def addRows(rows : ResponseRows) : Unit = withTableId(rows.id, table => table.addRows(rows))
  def addForeignKeys(keys : ResponseForeignKeys) : Unit =  withTableId(keys.id, table => table.addForeignKeys(keys)) 
  def addColumns(columns : ResponseColumns) : Unit =  addBrowsingTable(createTable(columns.tableName,columns.columns))
  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  addBrowsingTable(createTableFollow(columns.tableName,columns.columns, columns.follow))
}

