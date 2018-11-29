package dbtarzan.db.actor

import java.sql.SQLException
import akka.actor.Actor
import akka.actor.ActorRef
import java.time.LocalDateTime
import scala.collection.mutable.HashMap

import dbtarzan.db.util.ExceptionToText
import dbtarzan.config.ConnectionData
import dbtarzan.db._
import dbtarzan.messages._
import dbtarzan.db.ForeignKeysToFile


/* The actor that reads data from the database */
class DatabaseWorker(createConnection : ConnectionProvider, data : ConnectionData, guiActor : ActorRef) extends Actor {
	def databaseName = data.name
	def databaseId = DatabaseId(data.name)	
	val log = new Logger(guiActor)
	var optCore :Option[DatabaseWorkerCore] = buildCore()
	val foreignKeysFromFile = HashMap.empty[String, ForeignKeys]
	val cache = new DatabaseWorkerCache()
	loadForeignKeysFromFile()	

	private def buildCore() : Option[DatabaseWorkerCore] = try {
			val connection = createConnection.getConnection(data)
			log.info("Connected to "+databaseName) 
			Some(new DatabaseWorkerCore(connection, data.schema))
		} 
		catch { 
			case se : SQLException => { 
				log.error("Cronnecting to the database "+databaseName+" got "+ExceptionToText.sqlExceptionText(se), se) 
				None
			}
			case e : Exception => { 
				log.error("Cronnecting to the database "+databaseName+" got", e) 
				None
			}
		}

	private def loadForeignKeysFromFile() : Unit = 
		if(ForeignKeysToFile.fileExist(databaseName)) {
			log.info("Loading foreign keys from the database file "+databaseName)
			try
			{
				val tablesKeys = ForeignKeysToFile.fromFile(databaseName)
				tablesKeys.keys.foreach(tableKeys => foreignKeysFromFile += tableKeys.table -> tableKeys.keys)
			} 
			catch { 
				case e : Exception => log.error("Reading the keys file for database "+databaseName+" got the following error. Delete the file if it is corrupted or of an old version of the system.", e) 
			}
		}

	/* handles the exceptions sending the exception messages to the GUI */
	private def handleErr[R](errHandler : Exception => Unit, operation: => R): Unit = 
	    try { operation } catch {
	      case e : Exception => errHandler(e)
	    }

	/* if conneced execure the operation, otherwise send an error to the GUI */
	private def withCore[R](errHandler : Exception => Unit, operation: DatabaseWorkerCore => R): Unit =
		optCore match {
			case Some(core) => handleErr(errHandler, operation(core))
			case None => guiActor ! Error(LocalDateTime.now, "Database not connected", None)
		}

	private def logError(e: Exception) : Unit = log.error("dbWorker", e)
	

	override def  postStop() : Unit = {
		println("Actor for "+databaseName+" stopped")
	}

	private def close() : Unit = handleErr(logError, {
		println("Closing the worker for "+databaseName)
		guiActor ! ResponseCloseDatabase(databaseId)
		optCore.foreach(core =>
			core.closeConnection()
		)
		context.stop(self)
	})

	private def reset() : Unit = handleErr(logError, {
		println("Reseting the connection of the worker for "+databaseName)
		optCore.foreach(core =>
			try { core.closeConnection() } catch { case e : Throwable => {} }
			)
		optCore = buildCore()
		log.info("Connection to the database "+databaseName+" resetted")
	})		

	private def queryForeignKeys(qry : QueryForeignKeys) : Unit = withCore(logError, core => {
		val tableName = qry.queryId.tableId.tableName
		val foreignKeys = foreignKeysFromFile.getOrElseUpdate(tableName, 
			cache.cachedForeignKeys(tableName, core.foreignKeyLoader.foreignKeys(tableName))
		)
		guiActor ! ResponseForeignKeys(qry.queryId, foreignKeys)
	})

	private def queryRows(qry: QueryRows, maxRows: Option[Int]) : Unit = withCore(
		e => guiActor ! ErrorRows(qry.queryId, e), 
		core => core.queryLoader.query(qry.sql, maxRows.getOrElse(500),  rows => 
			guiActor ! ResponseRows(qry.queryId, rows)
		)
	)


	private def queryTables(qry: QueryTables) : Unit = withCore(logError, core => { 
			val names = core.tablesLoader.tableNames()
			if(!names.tableNames.isEmpty)
				log.info("Loaded "+names.tableNames.size+" tables from the database "+databaseName)
			else
				log.warning("No tables read from database "+databaseName+". Wrong schema?")
    		guiActor ! ResponseTables(qry.databaseId, names)
		})

	private def queryTablesByPattern(qry: QueryTablesByPattern) : Unit = withCore(logError, core => { 
			val names = core.tablesLoader.tablesByPattern(qry.pattern)
    		guiActor ! ResponseTables(qry.databaseId, names)
		})

	private def queryColumns(qry: QueryColumns) : Unit = withCore(logError, core => {
			val tableName = qry.tableId.tableName
			val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    		guiActor ! ResponseColumns(qry.tableId, columns, queryAttributes())
		})

	private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  withCore(logError, core => {
			val tableName = qry.tableId.tableName
			val columnsFollow = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    		guiActor ! ResponseColumnsFollow(qry.tableId, qry.follow, columnsFollow, queryAttributes())
    	})		

	private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = withCore(logError, core => {
			val tableName = qry.queryId.tableId.tableName
			val primaryKeys = cache.cachedPrimaryKeys(tableName, core.primaryKeysLoader.primaryKeys(tableName))
    		guiActor ! ResponsePrimaryKeys(qry.queryId, primaryKeys)
    	})

	private def queryAttributes() =  QueryAttributes(data.identifierDelimiters, data.schema)

  	def receive = {
	    case qry : QueryRows => queryRows(qry, data.maxRows)
	    case qry : QueryClose => close() 	    
	    case qry : QueryReset => reset() 	    
	    case qry : QueryTables => queryTables(qry) 
	    case qry : QueryTablesByPattern => queryTablesByPattern(qry) 
	    case qry : QueryColumns => queryColumns(qry)
	    case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
	    case qry : QueryForeignKeys => queryForeignKeys(qry)    	
		case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)    	
  	}
}