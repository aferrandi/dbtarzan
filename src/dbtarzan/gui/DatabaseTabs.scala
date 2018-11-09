package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab }
import scalafx.scene.Parent
import scala.collection.mutable.HashMap
import scalafx.Includes._
import scalafx.event.Event
import akka.actor.ActorRef
import dbtarzan.messages._
import dbtarzan.db.DatabaseId

case class DatabaseWithTab(database : Database, tab : Tab)

/** All the tabs with one database for each*/
class DatabaseTabs(guiWorker: => ActorRef, connectionsActor : => ActorRef) extends TDatabases with TControlBuilder {
  private val tabs = new TabPane()
  private val databaseById = HashMap.empty[DatabaseId, DatabaseWithTab]

  private def addDatabaseTab(dbActor : ActorRef, databaseId : DatabaseId) : Database = {
    println("add database tab for "+databaseId.databaseName)
    val database = new Database(dbActor, guiWorker, databaseId)
    val tab = buildTab(database)
    tabs += tab
    selectTab(tab)
    databaseById += databaseId -> DatabaseWithTab(database, tab)
    database
  }

  /* requests to close the connection to the database to the central database actor */
  private def sendClose(databaseId : DatabaseId) : Unit = {
    connectionsActor ! QueryClose(databaseId)     
  }
  /* build the GUI tab for the database */
  private def buildTab(database : Database) = new Tab() {
    text = database.getId.databaseName
    content = database.control
    onCloseRequest = (e : Event) => { sendClose(database.getId ) }
  }      

  /* requests to close all the database connections */
  def sendCloseToAllOpen() : Unit = 
    databaseById.keys.foreach(databaseId => sendClose(databaseId))

  /* utility method to do something (given by a closure) to a database */
  private def withDatabaseId(databaseId : DatabaseId, doWith : Database => Unit) : Unit =
    databaseById.get(databaseId).foreach(databaseWithTab => doWith(databaseWithTab.database))

  /* utility method to do something (given by a closure) to a table */
  private def withTableId(id : TableId, doWith : Database => Unit) : Unit = withDatabaseId(id.databaseId, doWith)

  /* received some rows coming as a result of a query, that have to be shown within a table tab */
  def addRows(rows : ResponseRows) : Unit = withTableId(rows.tableId, database => database.addRows(rows))

  /* received some foreign keys, that have to be shown within a table tab  */
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = withTableId(keys.tableId, database => database.addForeignKeys(keys)) 

  /* received the columns of a table, that are used to build the table in a tab  */
  def addColumns(columns : ResponseColumns) : Unit= withDatabaseId(columns.databaseId, database => database.addColumns(columns))

  /* received the columns of a table, that are used to build the table coming from the selection of a foreign key, in a tab */
  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit= withDatabaseId(columns.databaseId, database => database.addColumnsFollow(columns))

  /* received the primary keys of a table, that are used to mark columns as primary keys on a table */
  def addPrimaryKeys(keys : ResponsePrimaryKeys) : Unit= withTableId(keys.tableId, database => database.addPrimaryKeys(keys))

  /* received the list of the tables in the database, to show in the list on the left side */
  def addTables(tables : ResponseTables) : Unit = withDatabaseId(tables.databaseId, database => database.addTableNames(tables.names))

  /* received the data of a database, to open a database tab */
  def addDatabase(databaseData : ResponseDatabase) : Unit = {
    val database = addDatabaseTab(databaseData.dbActor, databaseData.databaseId)
    databaseData.dbActor ! QueryTables(database.getId)
  }

  /* from the database name, finds out the tab to which send the information (tables, columns, rows) */
  private def getTabByDatabaseId(databaseId : DatabaseId) = 
    tabs.tabs.filter(_.text == databaseId.databaseName).headOption

  /* selects and shows the content of a database tab */
  private def selectTab(tab : Tab) : Unit = 
    tabs.selectionModel().select(tab)

  def requestRemovalTabsAfter(request : RequestRemovalTabsAfter) : Unit = withTableId(request.tableId, database => database.requestRemovalTabsAfter(request.tableId))

  def requestRemovalTabsBefore(request : RequestRemovalTabsBefore) : Unit = withTableId(request.tableId, database => database.requestRemovalTabsBefore(request.tableId))

  def requestRemovalAllTabs(request : RequestRemovalAllTabs) : Unit = withDatabaseId(request.databaseId, database => database.requestRemovalAllTabs())

  def copySelectionToClipboard(copy : CopySelectionToClipboard) : Unit = withTableId(copy.tableId, database => database.copySelectionToClipboard(copy))

  def copySQLToClipboard(copy : CopySQLToClipboard) : Unit = withTableId(copy.tableId, database => database.copySQLToClipboard(copy))

  def checkAllTableRows(check : CheckAllTableRows) : Unit = withTableId(check.tableId, database => database.checkAllTableRows(check))

  def checkNoTableRows(check :  CheckNoTableRows) : Unit = withTableId(check.tableId, database => database.checkNoTableRows(check))

  def switchRowDetails(switch: SwitchRowDetails) : Unit = withTableId(switch.tableId, database => database.switchRowDetails(switch))

  /* removes the database tab and its content */
  def removeDatabase(databaseToClose : ResponseCloseDatabase) : Unit = {
    val databaseId = databaseToClose.databaseId 
    databaseById -= databaseId
     val optTab = getTabByDatabaseId(databaseId)
     optTab.foreach(tab => tabs.tabs -= tab)
   }

  def removeTables(tablesToClose : ResponseCloseTables) : Unit = 
    withDatabaseId(tablesToClose.databaseId, database => database.removeTables(tablesToClose.ids))
  
  /* shows the tab of a database */
  def showDatabase(databaseId : DatabaseId) : Unit = {
    val optTab = getTabByDatabaseId(databaseId)
     optTab.foreach(tab => selectTab(tab))
  }

  def control : Parent = tabs

  def currentTableId : Option[TableId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    databaseById.values.find(_.tab == currentTab).map(_.database.currentTableId).flatten  
  }
}