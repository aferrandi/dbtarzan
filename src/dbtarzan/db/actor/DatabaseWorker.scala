package dbtarzan.db.actor

import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import akka.actor.ActorRef
import java.time.LocalDateTime
import scala.collection.mutable.HashMap

import dbtarzan.config.ConnectionData
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db._
import dbtarzan.messages._
import dbtarzan.db.util.FileReadWrite
import dbtarzan.db.ForeignKeysToFile


/* The actor that reads data from the database */
class DatabaseWorker(createConnection : ConnectionProvider, data : ConnectionData, guiActor : ActorRef) extends Actor {
	def databaseName = data.name
	var core = buildCore()
	val foreignKeysCache = HashMap.empty[String, ForeignKeys]
	val log = new Logger(guiActor)
	loadForeignKeysFromFile()	

	private def buildCore() : DatabaseWorkerCore = try {
			new DatabaseWorkerCore(createConnection.getConnection(data), data.schema)
		} 
		catch { case e : Exception => { 
			log.error("Cronnecting to the database "+databaseName+" got", e) 
			throw e 
		}}

	private def loadForeignKeysFromFile() : Unit = 
		if(ForeignKeysToFile.fileExist(databaseName)) {
			log.info("Loading foreign keys from the database file "+databaseName)
			try
			{
				val tablesKeys = ForeignKeysToFile.fromFile(databaseName)
				tablesKeys.keys.foreach(tableKeys => foreignKeysCache += tableKeys.table -> tableKeys.keys)
			} 
			catch { case e : Exception => log.error("Reading the keys file for database "+databaseName+" got the following error. Delete the file if it is corrupted or of an old version of the system.", e) }
		}

	/* gets the columns of a table from the database metadata */
	private def columnNames(tableName : String) : Fields = {
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
	private def tableNames() : TableNames = {
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
	      case e : Exception => guiActor ! Error(LocalDateTime.now, "dbWorker", e)
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
		log.info("Connection to the database "+databaseName+" resetted")
	})		

	private def queryForeignKeys(qry : QueryForeignKeys) : Unit = handleErr({
		val tableName = qry.id.tableName
		val foreignKeys = foreignKeysCache.getOrElseUpdate(tableName, core.foreignKeyLoader.foreignKeys(tableName))
		guiActor ! ResponseForeignKeys(qry.id, foreignKeys)
	})

	private def queryRows(qry: QueryRows, maxRows: Option[Int]) : Unit = handleErr(
		core.queryLoader.query(qry, maxRows.getOrElse(500),  rows => 
			guiActor ! ResponseRows(qry.id, rows)
			))

	private def queryTables(qry: QueryTables) : Unit = handleErr(
    		guiActor ! ResponseTables(qry.id, tableNames())
		)


	private def queryColumns(qry: QueryColumns) : Unit = handleErr( 
    		guiActor ! ResponseColumns(qry.id, qry.tableName, columnNames(qry.tableName), queryAttributes())
    	)

	private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  handleErr(
    		guiActor ! ResponseColumnsFollow(qry.id, qry.tableName, qry.follow, columnNames(qry.tableName), queryAttributes())
    	)		

	private def queryAttributes() =  QueryAttributes(data.identifierDelimiters, data.schema)


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