package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip, ContextMenu, MenuItem}
import scalafx.event.ActionEvent
import dbtarzan.messages._
import scala.collection.mutable.HashMap
import akka.actor.ActorRef
import dbtarzan.db.{Fields, TableDescription, FollowKey, ForeignKeyMapper}
import dbtarzan.gui.util.JFXUtil
import scalafx.Includes._

/**
  One tab for each table
*/
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseName : String) extends TTables {
  val tabs = new TabPane()
  val mapTable = HashMap.empty[TableId, BrowsingTable]

  private def createTable(tableName : String, columns : Fields) : dbtarzan.db.Table = 
    dbtarzan.db.Table.build(TableDescription(tableName, None, None), columns)

  private def createTableFollow(tableName : String, columns : Fields, follow : FollowKey) : dbtarzan.db.Table = {
    println("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns) 
  }

  private def buildTab(dbTable : dbtarzan.db.Table, browsingTable :  BrowsingTable) = new Tab() {      
      text = buildTabText(dbTable)
      content = browsingTable.layout     
      tooltip.value = Tooltip(dbTable.sql)
      contextMenu = new ContextMenu(buildClipboardMenu(dbTable))
    }

  private def buildClipboardMenu(dbTable : dbtarzan.db.Table) = new MenuItem {
          text = "Copy SQL To Clipboard"
          onAction = (ev: ActionEvent) =>  try {
              JFXUtil.copyTextToClipboard(dbTable.sql)
            } catch {
              case ex: Exception => guiActor ! Error("Copying the query to the clipboard got", ex)
            }
      }

  /*
    Normally it shows the name of the table.

    If the tab is derived from a foreign key, it is in the form:
      [table] < [origin table]
      where origin table is the table on the other side of the foreign key

    If a filter (where) has been applied, a star (*) character is shown at the end of the text 
  */
  private def buildTabText(dbTable : dbtarzan.db.Table) : String = {
    def starForFilter(dbTable : dbtarzan.db.Table) = if(dbTable.hasFilter) " *" else ""

    val description = dbTable.tableDescription
    description.name + description.origin.map("<"+_).getOrElse("") + starForFilter(dbTable)
  } 

  def addBrowsingTable(dbTable : dbtarzan.db.Table) : Unit = {
    val browsingTable = new BrowsingTable(dbActor, guiActor, dbTable, databaseName)
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

