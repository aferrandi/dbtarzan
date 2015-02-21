package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab }
import dbtarzan.messages._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap
import scalafx.Includes._
import scalafx.event.Event
import akka.actor.{ ActorRef, ActorSystem }
/**
  All the tabs with one database for each
*/
class DatabaseTabs(system : ActorSystem) extends TDatabases {
  val tabs = new TabPane()
  val mapDatabase = HashMap.empty[String, Database]
  private def addDatabaseTab(dbActor : ActorRef, databaseName : String) : Database = {
          println("add database tab for "+databaseName)
          val database = new Database(dbActor, databaseName)
          val tab = buildTab(database)
          tabs += tab
          selectTab(tab)
          mapDatabase += databaseName -> database
          database
    }

  private def sendClose(databaseName : String) : Unit = {
        val configActor = system.actorFor("/user/configWorker")
        configActor ! QueryClose(databaseName)     
  }

  private def buildTab(database : Database) = new Tab() {
      text = database.getDatabaseName
      content = database.pane
      onCloseRequest = (e : Event) => { sendClose(database.getDatabaseName) }
  }      

  def sendCloseToAllOpen() : Unit = 
      mapDatabase.keys.foreach(databaseName => sendClose(databaseName))

  private def withDatabaseName(databaseName : String, doWith : Database => Unit) : Unit =
    mapDatabase.get(databaseName).foreach(database => doWith(database))

  private def withTableId(id : TableId, doWith : Database => Unit) : Unit = withDatabaseName(id.databaseName, doWith)

  private def withDatabaseId(id : DatabaseId, doWith : Database => Unit) : Unit = withDatabaseName(id.databaseName, doWith)

  def addRows(rows : ResponseRows) : Unit = withTableId(rows.id, database => database.tableTabs.addRows(rows))

  def addForeignKeys(keys : ResponseForeignKeys) : Unit = withTableId(keys.id, database => database.tableTabs.addForeignKeys(keys)) 

  def addColumns(columns : ResponseColumns) : Unit= withDatabaseId(columns.id, database => database.tableTabs.addColumns(columns))

  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit= withDatabaseId(columns.id, database => database.tableTabs.addColumnsFollow(columns))

  def addTables(tables : ResponseTables) : Unit = withDatabaseId(tables.id, database => database.addTableNames(tables.names))

  def addDatabase(databaseData : ResponseDatabase) : Unit = {
      val database = addDatabaseTab(databaseData.dbActor, databaseData.databaseName)
      databaseData.dbActor ! QueryTables(database.id)
    }

  private def getTabByDatabaseName(databaseName : String) = 
    tabs.tabs.filter(_.text == databaseName).headOption

  private def selectTab(tab : Tab) : Unit = 
    tabs.selectionModel().select(tab)

  def removeDatabase(databaseToClose : ResponseClose) : Unit = {
    val databaseName = databaseToClose.databaseName 
    mapDatabase -= databaseName
     val optTab = getTabByDatabaseName(databaseName)
     optTab.foreach(tab => tabs.tabs -= tab)
   }

  def showDatabase(databaseName : String) : Unit = {
    val optTab = getTabByDatabaseName(databaseName)
     optTab.foreach(tab => selectTab(tab))
  }
}