package dbtarzan.db.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.VirtualForeignKeyToForeignKey
import dbtarzan.db.util.ExceptionToText
import dbtarzan.localization.Localization
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
  localization: Localization,
  keyFilesDirPath: Path,
  loginPasswords: LoginPasswords
  ) extends Actor {
  private val createConnection = new DriverManagerWithEncryption(encryptionKey)
  private val log = new Logger(guiActor)
  private var optCores : Option[Map[SimpleDatabaseId, DatabaseCore]] = buildCores()
  if(optCores.isEmpty)
    closeThisDBWorker()
  private val cache = new DatabaseWorkerCache()
  private val virtualKeysFromFile = new DatabaseVirtualKeysFromFile(databaseId, localization, keyFilesDirPath, log)
  private val virtualKeysToFile = new DatabaseVirtualKeysToFile(databaseId, localization, keyFilesDirPath, log)
  private val foreignKeysForCache  : mutable.HashMap[TableId, ForeignKeys] = buildForeignKeysForCache()
  private var virtualForeignKeys : List[VirtualalForeignKey] = virtualKeysFromFile.loadVirtualForeignKeysFromFile()
  private var virtualForeignKeysExploded : Map[TableId, ForeignKeys] = VirtualForeignKeyToForeignKey.toForeignKeys(virtualForeignKeys)

  private def buildOneCore(data: ConnectionData, loginPassword: Option[Password]) : Option[DatabaseCore] = try {
    val connection = createConnection.getConnection(data, loginPassword)
    setReadOnlyIfPossible(connection)
    log.info(localization.connectedTo(databaseIdText(databaseId)))
    val simpleDatabaseId = SimpleDatabaseId(data.name)
    val schemaId = data.schema.map(schema => SchemaId(databaseId, simpleDatabaseId, schema))
    val attributes = QueryAttributes(data.identifierDelimiters, DBDefinition(schemaId, data.catalog), data.maxFieldSize)
    val limits = DBLimits(data.maxRows, data.queryTimeoutInSeconds)
    Some(new DatabaseCore(connection, databaseId, simpleDatabaseId, attributes, limits, log))
  } catch {
    case se : SQLException => {
      log.error(localization.errorConnectingToDatabase(DatabaseIdUtil.databaseIdText(databaseId))+" "+ExceptionToText.sqlExceptionText(se), se)
      None
    }
    case e : Exception => {
      log.error(localization.errorConnectingToDatabase(DatabaseIdUtil.databaseIdText(databaseId)), e)
      None
    }
  }

  private def buildForeignKeysForCache(): mutable.HashMap[TableId, ForeignKeys] = {
    val keysForAllSimpleDatabases = datas.flatMap(data => {
      val foreignFromFile = new DatabaseForeignKeysFromFile(databaseId, SimpleDatabaseId(data.name), localization, keyFilesDirPath, log)
      foreignFromFile.loadForeignKeysFromFile()
    })
    mutable.HashMap(keysForAllSimpleDatabases.toSeq*)
  }


  private def buildCores(): Option[Map[SimpleDatabaseId, DatabaseCore]] = {
    val cores = datas.map(data => buildOneCore(data, loginPasswords.loginPasswords.get(SimpleDatabaseId(data.name))))
    if(cores.contains(None))
      None
    else
      Some(cores.map(core => core.get.simpleDatabaseId -> core.get).toMap)
  }

  private def setReadOnlyIfPossible(connection: Connection): Unit = {
    try {
      connection.setReadOnly(true)
    } catch {
      case _: Exception => None
    }
  }



  /* if connected execure the operation, otherwise send an error to the GUI */
  private def withCore[R](simpleDatabaseId: SimpleDatabaseId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    optCores match {
      case Some(cores) => cores.get(simpleDatabaseId).foreach(
        core => ExceptionToHandler.handleErr(errHandler, operation(core))
      )
      case None => guiActor ! Error(LocalDateTime.now, "Database not connected", None)
    }

  private def withCore[R](queryId: QueryId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    withCore(queryId.tableId, operation, errHandler)

  private def withCore[R](tableId: TableId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    withCore(tableId.simpleDatabaseId, operation, errHandler)

  private def withCores[R](operation: List[DatabaseCore] => Any, errHandler: Exception => Unit): Unit =
    optCores match {
      case Some(cores) => ExceptionToHandler.handleErr(errHandler, operation(cores.values.toList))
      case None => guiActor ! Error(LocalDateTime.now, "Database not connected", None)
    }

  private def logError(e: Exception) : Unit = log.error("dbWorker", e)

  override def postStop() : Unit = {
    log.debug(s"Actor for ${DatabaseIdUtil.databaseIdText(databaseId)} stopped")
  }

  private def close() : Unit = ExceptionToHandler.handleErr(logError, {
    log.debug(s"Closing the database worker for ${DatabaseIdUtil.databaseIdText(databaseId)}")
    guiActor ! ResponseCloseDatabase(databaseId)
    closeCore()
    context.stop(self)
  })

  private def reset() : Unit = ExceptionToHandler.handleErr(logError, {
    log.debug(s"ReseFting the connection of the database worker for ${DatabaseIdUtil.databaseIdText(databaseId)}")
    closeCore()
    optCores = buildCores()
    log.info(localization.connectionResetted(databaseIdText(databaseId)))
  })

  private def closeCore(): Unit = {
    optCores.foreach(cores =>
        cores.values.foreach(core =>
          try {
            core.closeConnection()
          } catch {
            case _: Throwable =>
          }
        )
      )
    optCores = None
  }


  private def queryForeignKeys(qry : QueryForeignKeys) : Unit = withCore(qry.queryId, core => {
      val tableId = qry.queryId.tableId
      val foreignKeys = ForeignKeys(
          foreignKeysForCache.getOrElseUpdate(tableId,
              cache.cachedForeignKeys(tableId, core.foreignKeyLoader.foreignKeys(tableId))
          ).keys ++ virtualForeignKeysExploded.get(tableId).map(_.keys).getOrElse(List.empty))
      guiActor ! ResponseForeignKeys(qry.queryId, qry.structure, foreignKeys)
    }, logError)

  private def queryRows(qry: QueryRows) : Unit = withCore(qry.queryId, core => {
    val sql = SqlBuilder.buildSql(qry.structure)
    val maxRows = core.limits.maxRows.getOrElse(500)
    val queryTimeouts = core.limits.queryTimeoutInSeconds.map(_.seconds).getOrElse(10 seconds)
    core.queryLoader.query(sql, maxRows, queryTimeouts, core.attributes.maxFieldSize, qry.structure.columns, rows =>
        guiActor ! ResponseRows(qry.queryId, qry.structure, rows)
      )
  }, e => queryRowsHandleErr(qry, e))
  private def queryRowsHandleErr(qry: QueryRows, e: Exception): Unit =
    qry.original match {
      case Some(original) => guiActor ! ErrorRows(original.queryId, e)
      case None => log.error(localization.errorQueryingDatabase(databaseIdText(databaseId)), e)
    }

  private def queryTables(qry: QueryTables) : Unit = withCores(cores => {
      val tableIds = cores.flatMap(core =>
        tableNamesToTableIds(core.simpleDatabaseId, core.tablesLoader.tableNames())
      ).toList
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

  private def querySchemas(qry: QuerySchemas) : Unit = withCores(cores => {
    guiActor ! ResponseSchemas(qry.databaseId, SchemaIds(schemaIds(cores)))
  }, ex => {
    logError(ex)
    closeThisDBWorker()
  })

  private def closeThisDBWorker(): Unit = {
    log.debug("Send QueryClose to connection actor to close this DBWorker")
    connectionActor ! QueryClose(databaseId)
  }

  private def queryTablesByPattern(qry: QueryTablesByPattern) : Unit = withCores(cores => {
        val tableIds = cores.flatMap(core => tableNamesToTableIds(core.simpleDatabaseId, core.tablesLoader.tablesByPattern(qry.pattern)))
        guiActor ! ResponseTablesByPattern(qry.databaseId, TableIds(tableIds))
      }, logError)

  private def queryColumns(qry: QueryColumns) : Unit = withCore(qry.tableId, core => {
      val tableName = qry.tableId.tableName
      val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
        guiActor ! ResponseColumns(qry.tableId, columns, core.attributes)
    }, logError)

  private def queryIndexes(qry: QueryIndexes) : Unit = withCore(qry.queryId, core => {
    val tableName = qry.queryId.tableId.tableName
    val indexes = cache.cachedIndexes(tableName, core.indexesLoader.indexes(tableName))
    guiActor ! ResponseIndexes(qry.queryId, indexes)
  }, logError)

  private def queryColumnsFollow(qry: QueryColumnsFollow) : Unit =  withCore(qry.tableId, core => {
    val tableName = qry.tableId.tableName
    val columnsFollow = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
      guiActor ! ResponseColumnsFollow(qry.tableId, qry.follow, columnsFollow, core.attributes)
    }, logError)

  private def queryColumnsForForeignKeys(qry: QueryColumnsForForeignKeys) : Unit = withCore(qry.tableId, core => {
    val tableName = qry.tableId.tableName
    val columns = cache.cachedFields(tableName, core.columnsLoader.columnNames(tableName))
    guiActor ! ResponseColumnsForForeignKeys(qry.tableId, columns)
  }, logError)

  private def queryPrimaryKeys(qry: QueryPrimaryKeys) : Unit = withCore(qry.queryId, core => {
    val tableName = qry.queryId.tableId.tableName
    val primaryKeys = cache.cachedPrimaryKeys(tableName, core.primaryKeysLoader.primaryKeys(tableName))
    println(s"Primary keys ${primaryKeys}")
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

  def queryOneRow(qry: QueryOneRow): Unit = withCore(qry.queryId, core => {
    val queryTimeput = core.limits.queryTimeoutInSeconds.map(_.seconds).getOrElse(10 seconds)
    core.queryLoader.query(SqlBuilder.buildSql(qry.structure), 1, queryTimeput, None, qry.structure.columns, rows =>
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
    case qry : QueryPrimaryKeys => queryPrimaryKeys(qry)
    case qry : QuerySchemas => querySchemas(qry)
    case qry : QueryIndexes => queryIndexes(qry)
    case _: RequestVirtualForeignKeys => requestVirtualForeignKeys()
    case update: UpdateVirtualForeignKeys => updateVirtualForeignKeys(update)
  }
}