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
	val log = new Logger(guiActor)
	var optCore :Option[DatabaseWorkerCore] = buildCore()
	val foreignKeysCache = HashMap.empty[String, ForeignKeys]
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
			log.info("Loadruning foreign keys from the database file "+databaseName)
			try
			{
				val tablesKeys = ForeignKeysToFile.fromFile(databaseName)
				tablesKeys.keys.foreach(tableKeys => foreignKeysCache += tableKeys.table -> tableKeys.keys)
			} 
			catch { 
				case e : Exception => log.error("Reading the keys file for database "+databaseName+" got the following error. Delete the file if it is corrupted or of an old version of the system.", e) 
			}
		}

	/* handles the exceptions sending the exception messages to the GUI */
	private def handleErr[R](operation: => R): Unit = 
	    try { operation } catch {
	      case e : Exception => guiActor ! Error(LocalDateTime.now, "dbWorker", e)
	    }

	/* if conneced execure the operation, otherwise send an error to the GUI */
	private def withCore[R](operation: DatabaseWorkerCore => R): Unit =
		optCore match {
			case Some(core) =>  handleErr( operation(core) )
			case None => guiActor ! Error(LocalDateTime.now, "dbWorker", new Exception("Database not connected"))
		}


	override def  postStop() : Unit = {
		println("Actor for "+databaseName+" stopped")
	}

	private def close() : Unit = handleErr({
		println("Closing the worker for "+databaseName)
		guiActor ! ResponseCloseDatabase(databaseName)
		optCore.foreach(core =>
			core.closeConnection()
		)
		context.stop(self)
	})

	private def reset() : Unit = handleErr({
		println("Reseting the connection of the worker for "+databaseName)
		optCore.foreach(core =>
			try { core.closeConnection() } catch { case e : Throwable => {} }
			)
		optCore = buildCore()
		log.info("Connection to the database "+databaseName+" resetted")
	})		

	private def queryForeignKeys(qry : QueryForeignKeys) : Unit = withCore(core => {
		val tableName = qry.id.tableName
		val foreignKeys = foreignKeysCache.getOrElseUpdate(tableName, 
			cache.cachedForeignKeys(tableName, core.foreignKeyLoader.foreignKeys(tableName))
		)
		guiActor ! ResponseForeignKeys(qry.id, foreignKeys)
	})

	private def queryRows(qry: QueryRows, maxRows: Option[Int]) : Unit = withCore(core => 
		core.queryLoader.query(qry, maxRows.getOrElse(500),  rows => 
			guiActor ! ResponseRows(qry.id, rows)
			))

	private def queryTables(qry: QueryTables) : Unit = withCore(core => { 
			val names = core.metadataLoader.tableNames()
			if(!names.tableNames.isEmpty)
				log.info("Loaded "+names.tableNames.size+" tables from the database "+databaseName)
			else
				log.warning("No tables read from database "+databaseName+". Wrong schema?")
    		guiActor ! ResponseTables(qry.id, names)
		})

	private def queryColumns(qry: QueryColumns) : Unit = withCore(core => {
			val tableName = qry.tableName
			val columns = cache.cachedColumns(tableName, core.metadataLoader.columnNames(tableName))
    		guiActor ! ResponseColumns(qry.id, tableName, columns, queryAttributes())
		})

	private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  withCore(core => {
			val tableName = qry.tableName
			val columnsFollow = cache.cachedColumns(tableName, core.metadataLoader.columnNames(qry.tableName))
    		guiActor ! ResponseColumnsFollow(qry.id, tableName, qry.follow, columnsFollow, queryAttributes())
    	})		

	private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = withCore(core => {
			val tableName = qry.id.tableName
			val primaryKeys = cache.cachedPrimaryKeys(tableName, core.metadataLoader.primaryKeys(tableName))
    		guiActor ! ResponsePrimaryKeys(qry.id, primaryKeys)
    	})

	private def queryAttributes() =  QueryAttributes(data.identifierDelimiters, data.schema)

  	def receive = {
	    case qry : QueryRows => queryRows(qry, data.maxRows)
	    case qry : QueryClose => close() 	    
	    case qry : QueryReset => reset() 	    
	    case qry : QueryTables => queryTables(qry) 
	    case qry : QueryColumns => queryColumns(qry)
	    case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
	    case qry : QueryForeignKeys => queryForeignKeys(qry)    	
		case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)    	
  	}
}