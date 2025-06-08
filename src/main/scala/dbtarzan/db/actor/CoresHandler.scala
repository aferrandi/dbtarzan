package dbtarzan.db.actor

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.util.ExceptionToText
import dbtarzan.db.*
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.DatabaseIdUtil.databaseIdText
import dbtarzan.messages.{DatabaseIdUtil, QueryId}

import java.sql.{Connection, SQLException}

class CoresHandler(databaseId: DatabaseId,
                   encryptionKey : EncryptionKey,
                   datas : List[ConnectionData],
                   loginPasswords: LoginPasswords,
                   localization: Localization,
                   log: Logger) {
  private val createConnection = new DriverManagerWithEncryption(encryptionKey)
  private var optCores: Option[Map[SimpleDatabaseId, DatabaseCore]] = buildCores()
  
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

  private def buildOneCore(data: ConnectionData, loginPassword: Option[Password]): Option[DatabaseCore] = try {
    val connection = createConnection.getConnection(data, loginPassword)
    setReadOnlyIfPossible(connection)
    log.info(localization.connectedTo(databaseIdText(databaseId)))
    val simpleDatabaseId = SimpleDatabaseId(data.name)
    val schemaId = data.schema.map(schema => SchemaId(databaseId, simpleDatabaseId, schema))
    val attributes = QueryAttributes(data.identifierDelimiters, DBDefinition(schemaId, data.catalog), data.maxFieldSize, data.maxInClauseCount)
    val limits = DBLimits(data.maxRows, data.queryTimeoutInSeconds)
    Some(new DatabaseCore(connection, databaseId, simpleDatabaseId, attributes, limits, log))
  } catch {
    case se: SQLException => {
      log.error(localization.errorConnectingToDatabase(DatabaseIdUtil.databaseIdText(databaseId)) + " " + ExceptionToText.sqlExceptionText(se), se)
      None
    }
    case e: Exception => {
      log.error(localization.errorConnectingToDatabase(DatabaseIdUtil.databaseIdText(databaseId)), e)
      None
    }
  }

  def hasNoCores(): Boolean = optCores.isEmpty

  /* if connected execure the operation, otherwise send an error to the GUI */
  def withCore[R](simpleDatabaseId: SimpleDatabaseId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    optCores match {
      case Some(cores) => cores.get(simpleDatabaseId).foreach(
        core => ExceptionToHandler.handleErr(errHandler, operation(core))
      )
      case None => log.error("Database not connected")
    }

  def withCore[R](queryId: QueryId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    withCore(queryId.tableId, operation, errHandler)

  def withCore[R](tableId: TableId, operation: DatabaseCore => Any, errHandler: Exception => Unit): Unit =
    withCore(tableId.simpleDatabaseId, operation, errHandler)

  def withCores[R](operation: List[DatabaseCore] => Any, errHandler: Exception => Unit): Unit =
    optCores match {
      case Some(cores) => ExceptionToHandler.handleErr(errHandler, operation(cores.values.toList))
      case None => log.error("Database not connected")
    }

  private def closeCores(): Unit = {
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

  def reset(): Unit = {
    closeCores()
    optCores = buildCores()
  }

  def stop(): Unit =
    closeCores()
}
