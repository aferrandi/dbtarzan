package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab, Tooltip, ContextMenu, MenuItem}
import scalafx.scene.Parent
import scalafx.event.ActionEvent
import dbtarzan.messages._
import scala.collection.mutable.HashMap
import akka.actor.ActorRef
import dbtarzan.db.{Fields, TableDescription, FollowKey, ForeignKeyMapper, QueryAttributesApplier, QueryAttributes}
import scalafx.beans.property.StringProperty
import scalafx.Includes._

case class BrowsingTableWIthTab(table : BrowsingTable, tab : Tab)

/* One tab for each table */
class TableTabs(dbActor : ActorRef, guiActor : ActorRef, databaseId : DatabaseId) extends TTables with TControlBuilder {
  private val tabs = new TabPane()
  private val mapTable = HashMap.empty[TableId, BrowsingTableWIthTab]

  /* creates a table from scratch */
  private def createTable(tableName : String, columns : Fields, applier : QueryAttributesApplier) : dbtarzan.db.Table = 
    dbtarzan.db.Table.build(TableDescription(tableName, None, None), columns, applier)

  /* create a table that is given by following a foreign key of a table */  
  private def createTableFollow(tableName : String, columns : Fields, follow : FollowKey, applier : QueryAttributesApplier) : dbtarzan.db.Table = {
    println("table follow created "+columns)
    ForeignKeyMapper.toFollowTable(follow, columns, applier) 
  }

  private def buildTab(dbTable : dbtarzan.db.Table, browsingTable :  BrowsingTable) = new Tab() {      
      text = buildTabText(dbTable)
      content = browsingTable.control     
      tooltip.value = Tooltip("")
      contextMenu = new ContextMenu(
        ClipboardMenuMaker.buildClipboardMenu("SQL", () => dbTable.sql),
        buildRemoveBeforeMenu(this),
        buildRemoveAfterMenu(this),
        buildRemoveAllMenu()
     )
    }

  private def idsFromTabs(toCloseTabs : List[javafx.scene.control.Tab]) = 
    mapTable.filter({ case (id, tableAndTab) => toCloseTabs.contains(tableAndTab.tab.delegate)}).keys.toList

  private def buildRemoveAfterMenu(tab : Tab) = new MenuItem {
      text = "Close tabs after this"
      onAction = (ev: ActionEvent) => {
        val toCloseTabs = tabs.tabs.reverse.takeWhile(_ != tab.delegate).toList // need to check the javafx class
        val toCloseIds = idsFromTabs(toCloseTabs)
        guiActor ! ResponseCloseTables(databaseId, toCloseIds)
      }
    }

  private def buildRemoveBeforeMenu(tab : Tab) = new MenuItem {
      text = "Close tabs before this"
      onAction = (ev: ActionEvent) => {
        val toCloseTabs = tabs.tabs.takeWhile(_ != tab.delegate).toList // need to check the javafx class
        val toCloseIds = idsFromTabs(toCloseTabs)
        guiActor ! ResponseCloseTables(databaseId, toCloseIds)
      }
    }
 
  private def buildRemoveAllMenu() = new MenuItem {
      text = "Close all tabs"
      onAction = (ev: ActionEvent) => {
        val toCloseIds = idsFromTabs(tabs.tabs.toList)
        guiActor ! ResponseCloseTables(databaseId, toCloseIds)
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
    val browsingTable = new BrowsingTable(dbActor, guiActor, dbTable, databaseId)
    val tab = buildTab(dbTable, browsingTable)
    tabs += tab
    tabs.selectionModel().select(tab)
    browsingTable.onTextEntered(newTable => addBrowsingTable(newTable))
    mapTable += browsingTable.getId -> BrowsingTableWIthTab(browsingTable, tab)
  }

  private def withTableId(id : TableId, doWith : BrowsingTableWIthTab => Unit) : Unit =
    mapTable.get(id).foreach(tableAndTab => doWith(tableAndTab))

  def addRows(rows : ResponseRows) : Unit = 
     withTableId(rows.id, table => {
      table.table.addRows(rows)
      table.tab.tooltip.value.text = table.table.sql +" ("+table.table.rowsNumber+" rows)"
    })
  
  
  def addForeignKeys(keys : ResponseForeignKeys) : Unit =  withTableId(keys.id, table => table.table.addForeignKeys(keys)) 

  def addColumns(columns : ResponseColumns) : Unit =  addBrowsingTable(createTable(columns.tableName,columns.columns, QueryAttributesApplier.from(columns.queryAttributes)))

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit =  addBrowsingTable(createTableFollow(columns.tableName,columns.columns, columns.follow, QueryAttributesApplier.from(columns.queryAttributes)))
  
  def removeTables(ids : List[TableId]) : Unit = {
      val tabsToClose = mapTable.filterKeys(id => ids.contains(id)).values.map(_.tab.delegate)
      mapTable --= ids
      tabs.tabs --= tabsToClose
    }

  def control : Parent = tabs
}

