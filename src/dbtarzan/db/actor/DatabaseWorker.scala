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
class DatabaseWorker(createConnection : () => java.sql.Connection, data : ConnectionData, guiActor : ActorRef) extends Actor {
	def databaseName = data.name
	var core = buildCore()
	val foreignKeysCache = HashMap.empty[String, ForeignKeys]
	loadForeignKeysFromFile()	

	private def buildCore() = new DatabaseWorkerCore(createConnection(), data.schema)

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
		var meta = core.metadata()
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
		var meta = core.metadata()
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

	private def close() : Unit = handleErr({
		println("Closing the worker for "+databaseName)
		guiActor ! ResponseCloseDatabase(databaseName)
		core.closeConnection()
		context.stop(self)
	})

	private def reset() : Unit = handleErr({
		println("Reseting the connection of the worker for "+databaseName)
		try { core.closeConnection() } catch { case e : Throwable => {} }
		core = buildCore()
	})		

	private def queryForeignKeys(qry : QueryForeignKeys) : Unit = handleErr({
		val tableName = qry.id.tableName
		val foreignKeys = foreignKeysCache.getOrElseUpdate(tableName, core.foreignKeyLoader.foreignKeys(tableName))
		guiActor ! ResponseForeignKeys(qry.id, foreignKeys)
	})

	private def queryRows(qry: QueryRows, maxRows: Option[Int]) : Unit = handleErr(
		core.queryLoader.query(qry, maxRows.getOrElse(500), rows => 
			guiActor ! ResponseRows(qry.id, rows)
			))

	private def queryTables(qry: QueryTables) : Unit = handleErr(
    		guiActor ! ResponseTables(qry.id, tableNames())
		)

	private def queryColumns(qry: QueryColumns) : Unit = handleErr( 
    		guiActor ! ResponseColumns(qry.id, qry.tableName, columnNames(qry.tableName), data.identifierDelimiters)
    	)

	private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  handleErr(
    		guiActor ! ResponseColumnsFollow(qry.id, qry.tableName, qry.follow, columnNames(qry.tableName), data.identifierDelimiters)
    	)		


  	def receive = {
	    case qry : QueryRows => queryRows(qry, data.maxRows)
	    case qry : QueryClose => close() 	    
	    case qry : QueryReset => reset() 	    
	    case qry : QueryTables => queryTables(qry) 
	    case qry : QueryColumns => queryColumns(qry)
	    case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
	    case qry : QueryForeignKeys => queryForeignKeys(qry)    	
  	}
}