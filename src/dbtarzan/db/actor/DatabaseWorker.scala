package dbtarzan.db.actor

import java.sql.SQLException
import akka.actor.Actor
import akka.actor.ActorRef
import java.time.LocalDateTime
import scala.collection.mutable.HashMap

import dbtarzan.db.util.ExceptionToText
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db._
import dbtarzan.messages._
import dbtarzan.localization.Localization

/* The actor that reads data from the database */
class DatabaseWorker(
	encryptionKey : EncryptionKey, 
	data : ConnectionData, 
	guiActor : ActorRef, 
	localization: Localization
	) extends Actor {
	def databaseName = data.name
	def databaseId = DatabaseId(data.name)	
	val createConnection = new DriverManagerWithEncryption(encryptionKey)
	val log = new Logger(guiActor)
	var optCore :Option[DatabaseWorkerCore] = buildCore()
	val cache = new DatabaseWorkerCache()
	val fromFile = new DatabaseWorkerKeysFromFile(databaseName, localization, log)
	val toFile = new DatabaseWorkerKeysToFile(databaseName, localization, log)
	val foreignKeysForCache = HashMap(fromFile.loadForeignKeysForCache().toSeq: _*) 	
	val additionalForeignKeys = HashMap(fromFile.loadAdditionalForeignKeys().toSeq: _*) 		

	private def buildCore() : Option[DatabaseWorkerCore] = try {
			val connection = createConnection.getConnection(data)
			log.info(localization.connectedTo(databaseName)) 
			Some(new DatabaseWorkerCore(connection, DBDefinition(data.schema, data.catalog), localization))
		} 
		catch { 
			case se : SQLException => { 
				log.error(localization.errorConnectingToDatabase(databaseName)+" "+ExceptionToText.sqlExceptionText(se), se) 
				None
			}
			case e : Exception => { 
				log.error(localization.errorConnectingToDatabase(databaseName), e) 
				None
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
		log.info(localization.connectionResetted(databaseName))
	})		

	private def queryForeignKeys(qry : QueryForeignKeys) : Unit = withCore(logError, core => {
		val tableName = qry.queryId.tableId.tableName
		val foreignKeys = ForeignKeys(
				foreignKeysForCache.getOrElseUpdate(tableName, 
						cache.cachedForeignKeys(tableName, core.foreignKeyLoader.foreignKeys(tableName))
				).keys ++ additionalForeignKeys.get(tableName).map(_.keys).getOrElse(List.empty))
		guiActor ! ResponseForeignKeys(qry.queryId, foreignKeys)
	})

	private def queryRows(qry: QueryRows, maxRows: Option[Int]) : Unit = withCore(
		e => qry.original.foreach(original => guiActor ! ErrorRows(original.queryId, e)), 
		core => core.queryLoader.query(SqlBuilder.buildSql(qry.structure), maxRows.getOrElse(500),  rows => 
			guiActor ! ResponseRows(qry.queryId, qry.structure, rows, qry.original)
		)
	)

	private def queryTables(qry: QueryTables) : Unit = withCore(logError, core => { 
			val names = core.tablesLoader.tableNames()
			if(!names.tableNames.isEmpty)
				log.info(localization.loadedTables(names.tableNames.size, databaseName))
			else {
				val schemas = core.schemasLoader.schemasNames()
				val schemasText = schemas.schemas.map(_.name).mkString(", ")
				log.warning(localization.errorNoTables(databaseName, schemasText))
			}
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

	private def queryColumnsForForeignKeys(qry: QueryColumnsForForeignKeys) : Unit = withCore(logError, core => {
			val tableName = qry.tableName
			val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    		guiActor ! ResponseColumnsForForeignKeys(qry.databaseId, tableName, columns)
		})

	private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = withCore(logError, core => {
			val tableName = qry.queryId.tableId.tableName
			val primaryKeys = cache.cachedPrimaryKeys(tableName, core.primaryKeysLoader.primaryKeys(tableName))
    		guiActor ! ResponsePrimaryKeys(qry.queryId, primaryKeys)
    	})

	private def queryAttributes() =  QueryAttributes(data.identifierDelimiters, DBDefinition(data.schema, data.catalog))

	private def requestAdditionalForeignKeys(request : RequestAdditionalForeignKeys) : Unit = {
		val tableKeys = additionalForeignKeys.toList.map({ case (table, keys) => ForeignKeysForTable(table, keys) })
		guiActor ! AdditionalForeignKeys(databaseId, ForeignKeysForTableList(tableKeys))
	}

	private def updateAdditionalForeignKeys(update: UpdateAdditionalForeignKeys) : Unit = {
			update.keys.keys.foreach(ks =>  additionalForeignKeys.update(ks.table, ks.keys))
			toFile.saveAdditionalForeignKeys(update.keys)
	}

	def receive = {
		case qry : QueryRows => queryRows(qry, data.maxRows)
		case qry : QueryClose => close() 	    
		case qry : QueryReset => reset() 	    
		case qry : QueryTables => queryTables(qry) 
		case qry : QueryTablesByPattern => queryTablesByPattern(qry) 
		case qry : QueryColumns => queryColumns(qry)
		case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
		case qry : QueryColumnsForForeignKeys => queryColumnsForForeignKeys(qry) 
		case qry : QueryForeignKeys => queryForeignKeys(qry)    	
		case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)    	
		case request: RequestAdditionalForeignKeys => requestAdditionalForeignKeys(request)
		case update: UpdateAdditionalForeignKeys => updateAdditionalForeignKeys(update)
	}
}