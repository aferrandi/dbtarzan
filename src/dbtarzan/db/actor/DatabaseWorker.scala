package dbtarzan.db.actor

import java.sql.{Connection, ResultSet, DriverManager}
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import dbtarzan.config.ConnectionData
import dbtarzan.db.util.ResourceManagement.using
import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.messages._
import scala.collection.mutable.HashMap
import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.ForeignKeysToFile


/* The actor that reads data from the database */
class DatabaseWorker(connection : java.sql.Connection, data : ConnectionData, guiActor : ActorRef) extends Actor {
	def databaseName = data.name
	val foreignKeyLoader =  new ForeignKeyLoader(connection, data.schema)
	val foreignKeysCache = HashMap.empty[String, ForeignKeys]
	val queryLoader = new QueryLoader(connection)
	loadForeignKeysFromFile()	

	private def loadForeignKeysFromFile() : Unit = 
		if(ForeignKeysToFile.fileExist(databaseName)) {
			guiActor ! Info("Loading foreign keys from the database file "+databaseName)
			try
			{
				val tablesKeys = ForeignKeysToFile.fromFile(databaseName)
				tablesKeys.keys.foreach(tableKeys => foreignKeysCache += tableKeys.table -> tableKeys.keys)
			} 
			catch { case e : Exception => guiActor ! Error("Reading the keys file for database "+databaseName+" got", e) }
		}

	/* gets the columns of a table from the database metadata */
	def columnNames(tableName : String) : Fields = {
		var meta = connection.getMetaData()
		using(meta.getColumns(null, data.schema.orNull, tableName, "%")) { rs =>
			val list = new ListBuffer[Field]()			
			while(rs.next) {
				var fieldName = rs.getString("COLUMN_NAME")
				toType(rs.getInt("DATA_TYPE")).map(fieldType => list += Field(fieldName, fieldType))
			}
			println("Columns loaded")
			Fields(list.toList)
		}
	}

	/* gets all the tables in the database/schema from the database metadata */
	def tableNames() : TableNames = {
		var meta = connection.getMetaData()
		using(meta.getTables(null, data.schema.orNull, "%", Array("TABLE"))) { rs =>
			val list = new ListBuffer[String]()
			while(rs.next) {
				list += rs.getString("TABLE_NAME")			
			}
			TableNames(list.toList)
		}
	}

	/* converts the database column type to a DBTarzan internal type */
	private def toType(sqlType : Int) : Option[FieldType] = 
		sqlType match {
			case java.sql.Types.CHAR => Some(FieldType.STRING)
			case java.sql.Types.INTEGER => Some(FieldType.INT)
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => Some(FieldType.FLOAT)	
			case _ => Some(FieldType.STRING)
		}



	private def handleErr[R](r: => R): Unit = 
	    try { r } catch {
	      case e : Exception => guiActor ! Error("dbWorker", e)
	    }
	  

	override def  postStop() : Unit = {
		println("Actor for "+databaseName+" stopped")
	}

  	def receive = {
	    case qry : QueryRows => handleErr(
	    		queryLoader.query(qry, rows => 
	    			guiActor ! ResponseRows(qry.id, rows)
	    			)
	    	)
	    case qry : QueryClose => handleErr({
	    		println("Closing the worker for "+databaseName)
	    		guiActor ! ResponseCloseDatabase(databaseName)
	    		connection.close()
	    		context.stop(self)
	    	})	    
	    case qry: QueryTables => handleErr(
	    		guiActor ! ResponseTables(qry.id, tableNames())
			)
	    case qry : QueryColumns => handleErr( 
	    		guiActor ! ResponseColumns(qry.id, qry.tableName, columnNames(qry.tableName), data.identifierDelimiters)
	    	)
	    case qry : QueryColumnsFollow => handleErr(
	    		guiActor ! ResponseColumnsFollow(qry.id, qry.tableName, qry.follow, columnNames(qry.tableName), data.identifierDelimiters)
	    	)		
	    case qry: QueryForeignKeys => handleErr({
	    		val tableName = qry.id.tableName
	    		val foreignKeys = foreignKeysCache.getOrElseUpdate(tableName, foreignKeyLoader.foreignKeys(tableName))
	    		guiActor ! ResponseForeignKeys(qry.id, foreignKeys)
	    	})    	
    
  	}
}