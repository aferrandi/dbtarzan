package dbtarzan.gui

import scalafx.scene.control.{ TabPane, Tab }
import dbtarzan.messages._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.HashMap


/**
	All the tabs with one database for each
*/
class DatabaseTabs() extends TDatabases {
  val tabs = new TabPane()
  val mapDatabase = HashMap.empty[String, Database]

  def addDatabase(database : Database) : Unit = {
          val databaseName = database.getDatabaseName 	
	  			println("add database tab for "+databaseName)
          val tab = buildTab(database)
	  			tabs += tab
          tabs.selectionModel().select(tab)
          mapDatabase += databaseName -> database
  	}

  private def buildTab(database : Database) = new Tab() {
  		text = database.getDatabaseName
  		content = database.pane
		}  	

  private def withDatabaseName(databaseName : String, doWith : Database => Unit) : Unit =
    mapDatabase.get(databaseName).foreach(database => doWith(database))
  private def withTableId(id : TableId, doWith : Database => Unit) : Unit = withDatabaseName(id.databaseName, doWith)
  private def withDatabaseId(id : DatabaseId, doWith : Database => Unit) : Unit = withDatabaseName(id.databaseName, doWith)

  def addRows(rows : ResponseRows) : Unit = withTableId(rows.id, database => database.tableTabs.addRows(rows))
  def addForeignKeys(keys : ResponseForeignKeys) : Unit = withTableId(keys.id, database => database.tableTabs.addForeignKeys(keys)) 
  def addColumns(columns : ResponseColumns) : Unit= withDatabaseId(columns.id, database => database.tableTabs.addColumns(columns))
  def addColumnsFollow(columns : ResponseColumnsFollow) : Unit= withDatabaseId(columns.id, database => database.tableTabs.addColumnsFollow(columns))
  def addTables(tables : ResponseTables) : Unit = withDatabaseId(tables.id, database => database.addTableNames(tables.names))
}
