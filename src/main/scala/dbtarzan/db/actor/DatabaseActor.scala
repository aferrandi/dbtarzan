package dbtarzan.db.actor

import java.nio.file.Path
import java.sql.SQLException
import java.time.LocalDateTime

import akka.actor.{Actor, ActorRef}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db._
import dbtarzan.db.util.ExceptionToText
import dbtarzan.localization.Localization
import dbtarzan.messages._

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.language.postfixOps

/* The actor that reads data from the database */
class DatabaseActor(
	encryptionKey : EncryptionKey, 
	data : ConnectionData, 
	guiActor : ActorRef,
  connectionActor: ActorRef,
	localization: Localization,
	keyFilesDirPath: Path
	) extends Actor {
	private def databaseName = data.name
  private def databaseId = DatabaseId(data.name)
  private val createConnection = new DriverManagerWithEncryption(encryptionKey)
  private val log = new Logger(guiActor)
  private var optCore : Option[DatabaseWorkerCore] = buildCore()
  if(optCore.isEmpty)
    closeThisDBWorker()
  private val cache = new DatabaseWorkerCache()
  private val fromFile = new DatabaseWorkerKeysFromFile(databaseName, localization, keyFilesDirPath, log)
  private val toFile = new DatabaseWorkerKeysToFile(databaseName, localization, keyFilesDirPath, log)
  private val foreignKeysForCache  : mutable.HashMap[String, ForeignKeys] = mutable.HashMap(fromFile.loadForeignKeysFromFile().toSeq: _*)
  private var additionalForeignKeys : List[AdditionalForeignKey] = fromFile.loadAdditionalForeignKeysFromFile()
  private var additionalForeignKeysExploded : Map[String, ForeignKeys] = buildKeys(additionalForeignKeys)

  private def buildCore() : Option[DatabaseWorkerCore] = try {
    val connection = createConnection.getConnection(data)
    connection.setReadOnly(true)
    log.info(localization.connectedTo(databaseName))
    Some(new DatabaseWorkerCore(connection, DBDefinition(data.schema, data.catalog), data.maxFieldSize, localization, log))
  } catch {
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

	/* if connected execure the operation, otherwise send an error to the GUI */
	private def withCore[R](operation: DatabaseWorkerCore => Any, errHandler: Exception => Unit): Unit =
		optCore match {
			case Some(core) => handleErr(errHandler, operation(core))
			case None => guiActor ! Error(LocalDateTime.now, "Database not connected", None)
		}

	private def logError(e: Exception) : Unit = log.error("dbWorker", e)
	
	override def  postStop() : Unit = {
		log.debug("Actor for "+databaseName+" stopped")
	}

	private def close() : Unit = handleErr(logError, {
		log.debug("Closing the database worker for "+databaseName)
		guiActor ! ResponseCloseDatabase(databaseId)
    closeCore()
		context.stop(self)
	})

	private def reset() : Unit = handleErr(logError, {
		log.debug("Reseting the connection of the database worker for "+databaseName)
		closeCore()
		optCore = buildCore()
		log.info(localization.connectionResetted(databaseName))
	})

  private def closeCore(): Unit =
    optCore.foreach(core =>
      try {
        core.closeConnection()
        optCore = None
      } catch {
        case e: Throwable =>
      }
    )

  private def queryForeignKeys(qry : QueryForeignKeys) : Unit = withCore(core => {
      val tableName = qry.queryId.tableId.tableName
      val foreignKeys = ForeignKeys(
          foreignKeysForCache.getOrElseUpdate(tableName,
              cache.cachedForeignKeys(tableName, core.foreignKeyLoader.foreignKeys(tableName))
          ).keys ++ additionalForeignKeysExploded.get(tableName).map(_.keys).getOrElse(List.empty))
      guiActor ! ResponseForeignKeys(qry.queryId, foreignKeys)
    }, logError)

	private def queryRows(qry: QueryRows, maxRows: Option[Int], queryTimeout: Option[Duration], maxFieldSize: Option[Int]) : Unit = withCore(core =>
    core.queryLoader.query(SqlBuilder.buildSql(qry.structure), maxRows.getOrElse(500), queryTimeout.getOrElse(10 seconds),  maxFieldSize, rows =>
        guiActor ! ResponseRows(qry.queryId, qry.structure, rows, qry.original)
      ), e => queryRowsHandleErr(qry, e))

  private def queryRowsHandleErr(qry: QueryRows, e: Exception): Unit =
    qry.original match {
      case Some(original) => guiActor ! ErrorRows(original.queryId, e)
      case None => log.error(localization.errorQueryingDatabase(databaseName), e)
    }

  private def queryTables(qry: QueryTables) : Unit = withCore(core => {
        val names = core.tablesLoader.tableNames()
        if (names.tableNames.nonEmpty)
          log.info(localization.loadedTables(names.tableNames.size, databaseName))
        else {
          val schemas = core.schemasLoader.schemasNames()
          val schemasText = schemas.schemas.map(_.name).mkString(", ")
          log.warning(localization.errorNoTables(databaseName, schemasText))
        }
        guiActor ! ResponseTables(qry.databaseId, names, qry.dbActor)
      }, ex => {
      logError(ex)
      closeThisDBWorker()
    })

  private def querySchemas(qry: QuerySchemas) : Unit = withCore(core => {
    val schemas = core.schemasLoader.schemasNames()
    guiActor ! ResponseSchemas(qry.databaseId, schemas)
  }, ex => {
    logError(ex)
    closeThisDBWorker()
  })

  private def closeThisDBWorker(): Unit = {
    log.debug("Send QueryClose to connection actor to close this DBWorker")
    connectionActor ! QueryClose(databaseId)
  }

  private def queryTablesByPattern(qry: QueryTablesByPattern) : Unit = withCore(core => {
        val names = core.tablesLoader.tablesByPattern(qry.pattern)
        guiActor ! ResponseTablesByPattern(qry.databaseId, names)
      }, logError)

	private def queryColumns(qry: QueryColumns) : Unit = withCore(core => {
        val tableName = qry.tableId.tableName
        val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
          guiActor ! ResponseColumns(qry.tableId, columns, queryAttributes())
      }, logError)

  private def queryIndexes(qry: QueryIndexes) : Unit = withCore(core => {
    val tableName = qry.queryId.tableId.tableName
    val indexes = cache.cachedIndexes(tableName, core.indexesLoader.indexes(tableName))
    guiActor ! ResponseIndexes(qry.queryId, indexes)
  }, logError)

	private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  withCore(core => {
        val tableName = qry.tableId.tableName
        val columnsFollow = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
          guiActor ! ResponseColumnsFollow(qry.tableId, qry.follow, columnsFollow, queryAttributes())
        }, logError)

	private def queryColumnsForForeignKeys(qry: QueryColumnsForForeignKeys) : Unit = withCore(core => {
        val tableName = qry.tableName
        val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
          guiActor ! ResponseColumnsForForeignKeys(qry.databaseId, tableName, columns)
      }, logError)

	private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = withCore(core => {
        val tableName = qry.queryId.tableId.tableName
        val primaryKeys = cache.cachedPrimaryKeys(tableName, core.primaryKeysLoader.primaryKeys(tableName))
          guiActor ! ResponsePrimaryKeys(qry.queryId, primaryKeys)
        }, logError)

	private def queryAttributes() =
    QueryAttributes(data.identifierDelimiters, DBDefinition(data.schema, data.catalog), data.maxFieldSize)

	private def requestAdditionalForeignKeys(request : RequestAdditionalForeignKeys) : Unit = {
		guiActor ! ResponseAdditionalForeignKeys(databaseId, additionalForeignKeys)
	}

	private def updateAdditionalForeignKeys(update: UpdateAdditionalForeignKeys) : Unit = {
			additionalForeignKeys = update.keys
			additionalForeignKeysExploded = buildKeys(update.keys) 
			toFile.saveAdditionalForeignKeys(update.keys)
			val intersection = AdditionalForeignKeysIntersection.intersection(foreignKeysForCache, additionalForeignKeys)
			if(intersection.nonEmpty)
				log.error(localization.errorAFKAlreadyExisting(intersection)) 
	}

  private def buildKeys(keys: List[AdditionalForeignKey]) : Map[String, ForeignKeys] = {
    val keysStraight = keys.map(k => ForeignKey(k.name+"_straight", k.from, k.to, ForeignKeyDirection.STRAIGHT))
    val keysTurned = keys.map(k => ForeignKey(k.name+"_turned", k.to, k.from, ForeignKeyDirection.TURNED))
    (keysStraight ++ keysTurned).groupBy(_.from.table).mapValues(ForeignKeys).toMap
  }

  def queryOneRow(qry: QueryOneRow, queryTimeout: Option[FiniteDuration]): Unit = withCore(core =>
    core.queryLoader.query(SqlBuilder.buildSql(qry.structure), 1, queryTimeout.getOrElse(10 seconds),  None, rows =>
      guiActor ! ResponseOneRow(qry.queryId, qry.structure, rows.rows.head)
    ), e => guiActor ! ErrorRows(qry.queryId, e))

  def receive = {
		case qry : QueryRows => queryRows(qry, data.maxRows, data.queryTimeoutInSeconds.map(_.seconds), data.maxFieldSize)
    case qry : QueryOneRow => queryOneRow(qry, data.queryTimeoutInSeconds.map(_.seconds))  
		case qry : QueryClose => close() 	    
		case qry : QueryReset => reset() 	    
		case qry : QueryTables => queryTables(qry) 
		case qry : QueryTablesByPattern => queryTablesByPattern(qry) 
		case qry : QueryColumns => queryColumns(qry)
		case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
		case qry : QueryColumnsForForeignKeys => queryColumnsForForeignKeys(qry) 
		case qry : QueryForeignKeys => queryForeignKeys(qry)    	
		case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)
    case qry : QuerySchemas => querySchemas(qry)
    case qry : QueryIndexes => queryIndexes(qry)
    case request: RequestAdditionalForeignKeys => requestAdditionalForeignKeys(request)
		case update: UpdateAdditionalForeignKeys => updateAdditionalForeignKeys(update)
	}
}