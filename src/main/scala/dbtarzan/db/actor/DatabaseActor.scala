package dbtarzan.db.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.{ForeignKey, *}
import dbtarzan.db.foreignkeys.VirtualForeignKeyToForeignKey
import dbtarzan.db.sql.SqlBuilder
import dbtarzan.db.util.ExceptionToText
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.DatabaseIdUtil.databaseIdText
import dbtarzan.messages.*

import java.nio.file.Path
import java.sql.{Connection, SQLException}
import java.time.LocalDateTime
import scala.collection.mutable
import scala.concurrent.duration.*
import scala.language.postfixOps

/* The actor that reads data from the database */
class DatabaseActor(
  databaseId: DatabaseId,
  encryptionKey : EncryptionKey,
  datas : List[ConnectionData],
  guiActor : ActorRef,
  connectionActor: ActorRef,
  log: Logger,
  localization: Localization,
  keyFilesDirPath: Path,
  loginPasswords: LoginPasswords
  ) extends Actor {
  private val coreHandler = new CoresHandler(databaseId, encryptionKey, datas, loginPasswords, localization, log)
  if(coreHandler.hasNoCores())
    closeThisDBWorker()
  private val cache = new DatabaseWorkerCache()
  private val virtualKeysFromFile = new DatabaseVirtualKeysFromFile(databaseId, localization, keyFilesDirPath, log)
  private val virtualKeysToFile = new DatabaseVirtualKeysToFile(databaseId, localization, keyFilesDirPath, log)
  private val foreignKeysForCache  : mutable.HashMap[TableId, ForeignKeys] = buildForeignKeysForCache()
  private var virtualForeignKeys : List[VirtualalForeignKey] = virtualKeysFromFile.loadVirtualForeignKeysFromFile()
  private var virtualForeignKeysExploded : Map[TableId, ForeignKeys] = VirtualForeignKeyToForeignKey.toForeignKeys(virtualForeignKeys)

  private def buildForeignKeysForCache(): mutable.HashMap[TableId, ForeignKeys] = {
    val keysForAllSimpleDatabases = datas.flatMap(data => {
      val foreignFromFile = new DatabaseForeignKeysFromFile(databaseId, SimpleDatabaseId(data.name), localization, keyFilesDirPath, log)
      foreignFromFile.loadForeignKeysFromFile()
    })
    mutable.HashMap(keysForAllSimpleDatabases.toSeq*)
  }

  private def logError(e: Exception) : Unit = log.error("dbWorker", e)

  override def postStop() : Unit = {
    log.debug(s"Actor for ${DatabaseIdUtil.databaseIdText(databaseId)} stopped")
  }

  private def close() : Unit = ExceptionToHandler.handleErr(logError, {
    log.debug(s"Closing the database worker for ${DatabaseIdUtil.databaseIdText(databaseId)}")
    guiActor ! ResponseCloseDatabase(databaseId)
    coreHandler.stop()
    context.stop(self)
  })

  private def reset() : Unit = ExceptionToHandler.handleErr(logError, {
    log.debug(s"Resetting the connection of the database worker for ${DatabaseIdUtil.databaseIdText(databaseId)}")
    coreHandler.reset()
    log.info(localization.connectionResetted(databaseIdText(databaseId)))
  })


  private def queryForeignKeys(qry : QueryForeignKeys) : Unit = coreHandler.withCore(qry.queryId, core => {
      val tableId = qry.queryId.tableId
      val foreignKeys = ForeignKeys(extractForeignKeys(core, tableId))
      guiActor ! ResponseForeignKeys(qry.queryId, qry.structure, foreignKeys)
    }, logError)

  private def extractForeignKeys(core: DatabaseCore, tableId: TableId): List[ForeignKey] = {
    foreignKeysForCache.getOrElseUpdate(tableId,
      cache.cachedForeignKeys(tableId, core.foreignKeyLoader.foreignKeys(tableId))
    ).keys ++ virtualForeignKeysExploded.get(tableId).map(_.keys).getOrElse(List.empty)
  }


  private def queryForeignKeysByPattern(qry : QueryForeignKeysByPattern) : Unit = coreHandler.withCore(qry.queryId, core => {
    val tableId = qry.queryId.tableId
    val foreignKeys = ForeignKeys(ForeignKeysByPattern.filterForeignKeysByPattern(extractForeignKeys(core, tableId), qry.pattern))
    guiActor ! ResponseForeignKeysByPatterns(qry.queryId, foreignKeys)
  }, logError)



  private def queryRows(qry: QueryRows) : Unit = coreHandler.withCore(qry.queryId, core => {
    val sql = SqlBuilder.buildQuerySql(qry.structure)
    val maxRows = core.limits.maxRows.getOrElse(500)
    val queryTimeouts = calcQueryTimeouts(core)
    core.queryLoader.query(sql, maxRows, queryTimeouts, core.attributes.maxFieldSize, qry.structure.columns, rows =>
        guiActor ! ResponseRows(qry.queryId, qry.structure, rows)
      )
  }, e => queryRowsHandleErr(qry, e))

  private def queryRowsHandleErr(qry: QueryRows, e: Exception): Unit =
    qry.original match {
      case Some(original) => guiActor ! ErrorRows(original.queryId, e)
      case None => log.error(localization.errorQueryingDatabase(databaseIdText(databaseId)), e)
    }

  private def queryTables(qry: QueryTables) : Unit = coreHandler.withCores(cores => {
      val tableIds = cores.flatMap(core =>
        tableNamesToTableIds(core.simpleDatabaseId, core.tablesLoader.tableNames())
      )
      logTableNames(cores, tableIds)
      guiActor ! ResponseTables(qry.databaseId, TableIds(tableIds), qry.dbActor)
    }, ex => {
      logError(ex)
      closeThisDBWorker()
    })

  private def tableNamesToTableIds(simpleDatabaseId: SimpleDatabaseId, names: TableNames): List[TableId] = {
    names.names.map(name => TableId(databaseId, simpleDatabaseId, name))
  }

  private def logTableNames(cores: List[DatabaseCore], tableIds: List[TableId]): Unit = {
    if (tableIds.nonEmpty)
      log.info(localization.loadedTables(tableIds.size, databaseIdText(databaseId)))
    else {
      val schemas = schemaIds(cores)
      val schemasText = schemas.map(_.schema.schema).mkString(", ")
      log.warning(localization.errorNoTables(databaseIdText(databaseId), schemasText))
    }
  }

  private def schemaIds(cores: List[DatabaseCore]): List[SchemaId] = {
    cores.flatMap(core => core.schemasLoader.schemasNames().map(schemaName => SchemaId(databaseId, core.simpleDatabaseId, schemaName))).toList
  }

  private def querySchemas(qry: QuerySchemas) : Unit = coreHandler.withCores(cores => {
    guiActor ! ResponseSchemas(qry.databaseId, SchemaIds(schemaIds(cores)))
  }, ex => {
    logError(ex)
    closeThisDBWorker()
  })

  private def closeThisDBWorker(): Unit = {
    log.debug("Send QueryClose to connection actor to close this DBWorker")
    connectionActor ! QueryClose(databaseId)
  }

  private def queryTablesByPattern(qry: QueryTablesByPattern) : Unit = coreHandler.withCores(cores => {
      val tableIds = cores.flatMap(core => tableNamesToTableIds(core.simpleDatabaseId, core.tableAndColumnByPattern.tablesByPattern(qry.pattern)))
      guiActor ! ResponseTablesByPattern(qry.databaseId, TableIds(tableIds))
    }, logError)

  private def queryColumns(qry: QueryColumns) : Unit = coreHandler.withCore(qry.tableId, core => {
      val tableName = qry.tableId.tableName
      val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
        guiActor ! ResponseColumns(qry.tableId, columns, core.attributes)
    }, logError)

  private def queryIndexes(qry: QueryIndexes) : Unit = coreHandler.withCore(qry.queryId, core => {
    val tableName = qry.queryId.tableId.tableName
    val indexes = cache.cachedIndexes(tableName, core.indexesLoader.indexes(tableName))
    guiActor ! ResponseIndexes(qry.queryId, indexes)
  }, logError)

  private def queryRowsNumber(qry: QueryRowsNumber): Unit = coreHandler.withCore(qry.queryId, core => {
    val sql = SqlBuilder.buildCountSql(qry.structure)
    val queryTimeouts = calcQueryTimeouts(core)
    val rowsNumber = core.queryRowsNumberLoader.query(sql, queryTimeouts)
    guiActor ! ResponseRowsNumber(qry.queryId, rowsNumber)
  }, e => guiActor ! ErrorRows(qry.queryId, e))

  private def calcQueryTimeouts(core: DatabaseCore) = {
    core.limits.queryTimeoutInSeconds.map(_.seconds).getOrElse(10 seconds)
  }

  private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  coreHandler.withCore(qry.tableId, core => {
    val tableName = qry.tableId.tableName
    val columnsFollow = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    guiActor ! ResponseColumnsFollow(qry.tableId, qry.follow, columnsFollow, core.attributes)
  }, logError)

  private def queryColumnsForForeignKeys(qry: QueryColumnsForForeignKeys) : Unit = coreHandler.withCore(qry.tableId, core => {
    val tableName = qry.tableId.tableName
    val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    guiActor ! ResponseColumnsForForeignKeys(qry.tableId, columns)
  }, logError)

  private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = coreHandler.withCore(qry.queryId, core => {
    val tableName = qry.queryId.tableId.tableName
    val primaryKeys = cache.cachedPrimaryKeys(tableName, core.primaryKeysLoader.primaryKeys(tableName))
    log.debug(s"Primary keys ${primaryKeys}")
    guiActor ! ResponsePrimaryKeys(qry.queryId, qry.structure, primaryKeys)
  }, logError)

  private def requestVirtualForeignKeys() : Unit = {
    guiActor ! ResponseVirtualForeignKeys(databaseId, virtualForeignKeys)
  }

  private def updateVirtualForeignKeys(update: UpdateVirtualForeignKeys) : Unit = {
    virtualForeignKeys = update.keys
    virtualForeignKeysExploded = VirtualForeignKeyToForeignKey.toForeignKeys(update.keys)
    virtualKeysToFile.saveVirtualForeignKeys(update.keys)
    logVirtualForeignKeysErrorIfAlreadyExisting()
  }

  private def logVirtualForeignKeysErrorIfAlreadyExisting(): Unit = {
    val intersection = VirtualForeignKeysIntersection.intersection(foreignKeysForCache, virtualForeignKeys)
    if (intersection.nonEmpty)
      log.error(localization.errorAFKAlreadyExisting(intersection))
  }

  def queryOneRow(qry: QueryOneRow): Unit = coreHandler.withCore(qry.queryId, core => {
    val queryTimeput = calcQueryTimeouts(core)
    core.queryLoader.query(SqlBuilder.buildSingleRowSql(qry.structure), 1, queryTimeput, None, qry.structure.columns, rows =>
      guiActor ! ResponseOneRow(qry.queryId, qry.structure, rows.rows.head)
    )
  }, e => guiActor ! ErrorRows(qry.queryId, e))

  def receive: PartialFunction[Any,Unit] = {
    case qry : QueryRows => queryRows(qry)
    case qry : QueryOneRow => queryOneRow(qry)
    case _ : QueryClose => close()
    case _ : QueryReset => reset()
    case qry : QueryTables => queryTables(qry)
    case qry : QueryTablesByPattern => queryTablesByPattern(qry)
    case qry : QueryColumns => queryColumns(qry)
    case qry : QueryColumnsFollow =>  queryColumnsFollow(qry)
    case qry : QueryColumnsForForeignKeys => queryColumnsForForeignKeys(qry)
    case qry : QueryForeignKeys => queryForeignKeys(qry)
    case qry : QueryForeignKeysByPattern => queryForeignKeysByPattern(qry)
    case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)
    case qry : QuerySchemas => querySchemas(qry)
    case qry : QueryIndexes => queryIndexes(qry)
    case qry : QueryRowsNumber => queryRowsNumber(qry)
    case _: RequestVirtualForeignKeys => requestVirtualForeignKeys()
    case update: UpdateVirtualForeignKeys => updateVirtualForeignKeys(update)
  }
}