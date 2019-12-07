package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab }
import scalafx.scene.Parent
import scala.collection.mutable.HashMap
import scalafx.Includes._
import scalafx.event.Event
import akka.actor.ActorRef
import dbtarzan.messages._
import dbtarzan.db.{DatabaseId, TableId, TableNames}
import dbtarzan.localization.Localization

case class DatabaseWithTab(database : Database, tab : Tab)

/** All the tabs with one database for each*/
class DatabaseTabs(localization : Localization) extends TDatabases with TControlBuilder {
  private val tabs = new TabPane()
  private val databaseById = HashMap.empty[DatabaseId, DatabaseWithTab]
  private var guiActor: Option[ActorRef]  = None
  private var connectionsActor: Option[ActorRef] = None 

  def setActors(guiActor: ActorRef, connectionsActor: ActorRef) : Unit = {
      this.guiActor = Some(guiActor)
      this.connectionsActor = Some(connectionsActor)
  } 

  private def addDatabaseTab(dbActor : ActorRef, databaseId : DatabaseId, tableNames : TableNames) : Database = {
    println("add database tab for "+databaseId.databaseName)
    guiActor match {
      case Some(ga) => {
        val database = new Database(dbActor, ga, databaseId, localization, tableNames)
        val tab = buildTab(database)
        tabs += tab
        selectTab(tab)
        databaseById += databaseId -> DatabaseWithTab(database, tab)
        database
      }
      case None => throw new Exception("guiActor is not defined")
    }
  }

  /* requests to close the connection to the database to the central database actor */
  private def sendClose(databaseId : DatabaseId) : Unit = {
    connectionsActor.foreach(_ ! QueryClose(databaseId))     
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
  private def withQueryId(queryId : QueryId, doWith : Database => Unit) : Unit = 
    withDatabaseId(queryId.tableId.databaseId, doWith)

  /* utility method to do something (given by a closure) to a table */
  private def withTableId(tableId : TableId, doWith : Database => Unit) : Unit = 
    withDatabaseId(tableId.databaseId, doWith)


  def handleQueryIdMessage(msg: TWithQueryId) : Unit = 
    withQueryId(msg.queryId, database => database.handleQueryIdMessage(msg))

  def handleDatabaseIdMessage(msg: TWithDatabaseId) : Unit = msg match {
    case rsp : ResponseCloseDatabase => removeDatabase(rsp.databaseId)
    case rsp : ResponseDatabase => addDatabase(rsp.dbActor, rsp.databaseId) 
    case rsp : ResponseTables => addDatabaseTab(rsp.dbActor, rsp.databaseId, rsp.names)
    case _ => withDatabaseId(msg.databaseId, database => database.handleDatabaseIdMessage(msg))
  }

  def handleTableIdMessage(msg: TWithTableId) : Unit = 
    withTableId(msg.tableId, database => database.handleTableIdMessage(msg))

  /* received the data of a database, to open a database tab */
  def addDatabase(dbActor : ActorRef, databaseId : DatabaseId) : Unit =
    dbActor ! QueryTables(databaseId, dbActor)

  /* from the database name, finds out the tab to which send the information (tables, columns, rows) */
  private def getTabByDatabaseId(databaseId : DatabaseId) = 
    tabs.tabs.filter(_.text == databaseId.databaseName).headOption

  /* selects and shows the content of a database tab */
  private def selectTab(tab : Tab) : Unit = 
    tabs.selectionModel().select(tab)

  /* removes the database tab and its content */
  def removeDatabase(databaseId : DatabaseId) : Unit = {
    databaseById -= databaseId
     val optTab = getTabByDatabaseId(databaseId)
     optTab.foreach(tab => tabs.tabs -= tab)
   }
  
  /* shows the tab of a database */
  def showDatabase(databaseId : DatabaseId) : Unit = {
    val optTab = getTabByDatabaseId(databaseId)
     optTab.foreach(tab => selectTab(tab))
  }

  def control : Parent = tabs

  def currentTableId : Option[QueryId] = {
    val currentTab = tabs.selectionModel().selectedItem()
    databaseById.values.find(_.tab == currentTab).map(_.database.currentTableId).flatten  
  }
}