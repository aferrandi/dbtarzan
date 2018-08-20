package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab }
import scalafx.scene.Parent
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import scalafx.Includes._
import scalafx.event.Event
import akka.actor.{ ActorRef, ActorSystem }
import dbtarzan.messages._

/** All the tabs with one database for each*/
class DatabaseTabs(guiWorker: => ActorRef, connectionsActor : => ActorRef) extends TDatabases with TControlBuilder {
  private val tabs = new TabPane()
  private val mapDatabase = HashMap.empty[String, Database]

  private def addDatabaseTab(dbActor : ActorRef, databaseName : String) : Database = {
    println("add database tab for "+databaseName)
    val database = new Database(dbActor, guiWorker, databaseName)
    val tab = buildTab(database)
    tabs += tab
    selectTab(tab)
    mapDatabase += databaseName -> database
    database
  }

  /* requests to close the connection to the database to the central database actor */
  private def sendClose(databaseName : String) : Unit = {
    connectionsActor ! QueryClose(databaseName)     
  }
  /* build the GUI tab for the database */
  private def buildTab(database : Database) = new Tab() {
    text = database.getDatabaseName
    content = database.control
    onCloseRequest = (e : Event) => { sendClose(database.getDatabaseName) }
  }      

  /* requests to close all the database connections */
  def sendCloseToAllOpen() : Unit = 
      mapDatabase.keys.foreach(databaseName => sendClose(databaseName))

  /* utility method to do something (given by a closure) to a database */
  private def withDatabaseName(databaseName : String, doWith : Database => Unit) : Unit =
    mapDatabase.get(databaseName).foreach(database => doWith(database))

  /* utility method to do something (given by a closure) to a database */
  private def withDatabaseId(id : DatabaseId, doWith : Database => Unit) : Unit = withDatabaseName(id.databaseName, doWith)

  /* utility method to do something (given by a closure) to a table */
  private def withTableId(id : TableId, doWith : Database => Unit) : Unit = withDatabaseId(id.databaseId, doWith)

  /* received some rows coming as a result of a query, that have to be shown within a table tab */
  def addRows(rows : ResponseRows) : Unit = withTableId(rows.id, database => database.addRows(rows))

  /* received some foreign keys, that have to be shown within a table tab  */
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = withTableId(keys.id, database => database.addForeignKeys(keys)) 

  /* received the columns of a table, that are used to build the table in a tab  */
  def addColumns(columns : ResponseColumns) : Unit= withDatabaseId(columns.id, database => database.addColumns(columns))

  /* received the columns of a table, that are used to build the table coming from the selection of a foreign key, in a tab */
  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit= withDatabaseId(columns.id, database => database.addColumnsFollow(columns))

  /* received the list of the tables in the database, to show in the list on the left side */
  def addTables(tables : ResponseTables) : Unit = withDatabaseId(tables.id, database => database.addTableNames(tables.names))

  /* received the data of a database, to open a database tab */
  def addDatabase(databaseData : ResponseDatabase) : Unit = {
    val database = addDatabaseTab(databaseData.dbActor, databaseData.databaseName)
    databaseData.dbActor ! QueryTables(database.getId)
  }

  /* from the database name, finds out the tab to which send the information (tables, columns, rows) */
  private def getTabByDatabaseName(databaseName : String) = 
    tabs.tabs.filter(_.text == databaseName).headOption

  /* selects and shows the content of a database tab */
  private def selectTab(tab : Tab) : Unit = 
    tabs.selectionModel().select(tab)

  def requestRemovalTabsAfter(request : RequestRemovalTabsAfter) : Unit = withDatabaseId(request.databaseId, database => database.requestRemovalTabsAfter(request.tableId))

  def requestRemovalTabsBefore(request : RequestRemovalTabsBefore) : Unit = withDatabaseId(request.databaseId, database => database.requestRemovalTabsBefore(request.tableId))

  def requestRemovalAllTabs(request : RequestRemovalAllTabs) : Unit = withDatabaseId(request.databaseId, database => database.requestRemovalAllTabs())

  def copySelectionToClipboard(copy : CopySelectionToClipboard) : Unit = withTableId(copy.id, database => database.copySelectionToClipboard(copy))

  /* removes the database tab and its content */
  def removeDatabase(databaseToClose : ResponseCloseDatabase) : Unit = {
    val databaseName = databaseToClose.databaseName 
    mapDatabase -= databaseName
     val optTab = getTabByDatabaseName(databaseName)
     optTab.foreach(tab => tabs.tabs -= tab)
   }

  def removeTables(tablesToClose : ResponseCloseTables) : Unit = 
    withDatabaseId(tablesToClose.id, database => database.removeTables(tablesToClose.ids))
  
  /* shows the tab of a database */
  def showDatabase(databaseName : String) : Unit = {
    val optTab = getTabByDatabaseName(databaseName)
     optTab.foreach(tab => selectTab(tab))
  }

  def control : Parent = tabs
}