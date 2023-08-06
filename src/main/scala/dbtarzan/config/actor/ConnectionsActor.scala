package dbtarzan.config.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.{ConnectionData, ConnectionsConfig}
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db._
import dbtarzan.db.basicmetadata.MetadataSchemasLoader
import dbtarzan.localization.Localization
import dbtarzan.messages._

import java.nio.file.Path
import scala.collection.mutable

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsActor(datas : List[ConnectionData],
                       composites: List[Composite],
                       guiActor : ActorRef,
                       localization : Localization,
                       keyFilesDirPath : Path) extends Actor {
   private val mapDBWorker = mutable.HashMap.empty[DatabaseId, ActorRef]
   private var connectionsConfig = new ConnectionsConfig(datas)
   private var currentComposites : Map[CompositeId, Composite] = mapComposites(composites)

  private def mapComposites(composites: List[Composite]): Map[CompositeId, Composite] = {
    composites.map(composite => composite.compositeId -> composite).toMap
  }

  private val registerDriver = new RegisterDriver()
  private val log = new Logger(guiActor)

  private def datasFromDatabaseId(databaseId: DatabaseId): Option[List[ConnectionData]] = {
    databaseId.origin match {
      case Left(simpleDatabaseId) => Some(List(connectionsConfig.connectionDataFor(simpleDatabaseId)))
      case Right(compositeId) => currentComposites.get(compositeId).map(
        composite => composite.databaseIds.map(simpleDatabaseId => connectionsConfig.connectionDataFor(simpleDatabaseId))
      )
    }
  }

   /* creates the actors to serve the queries for a database */
  private def getDBActor(databaseId : DatabaseId, encriptionKey : EncryptionKey) : ActorRef = {
   datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val dbActor = ConnectionBuilder.buildDBActor(databaseId, registerDriver, datas, encriptionKey, guiActor, context, localization, keyFilesDirPath)
         mapDBWorker += databaseId -> dbActor
         dbActor
       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
     }
  }

  /* creates the actor to serve the creation of foreign keys text files and start the copy */
  private def startCopyWorker(databaseId : DatabaseId, encriptionKey : EncryptionKey) : Unit = {
   datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val copyActor = ConnectionBuilder.buildCopyWorker(databaseId, registerDriver, datas, encriptionKey, guiActor, context, localization, keyFilesDirPath)
         copyActor ! CopyToFile
       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
   }
  }

  /* if no actors are serving the queries to a specific database, creates them */
  private def queryDatabase(databaseId : DatabaseId, encriptionKey : EncryptionKey) : Unit = {
      log.debug("Querying the tables of the database "+DatabaseIdUtil.databaseIdText(databaseId))
      try {
        if(!mapDBWorker.isDefinedAt(databaseId)) {
          val dbWorker = getDBActor(databaseId, encriptionKey)
          dbWorker ! QueryTables(databaseId, dbWorker)
        } else
          guiActor ! ErrorDatabaseAlreadyOpen(databaseId)
    } catch {
      case e : Exception => {
        log.error(localization.errorQueryingDatabase(DatabaseIdUtil.databaseIdText(databaseId)), e)
        e.printStackTrace()
      }
    }
  }

  def extractSchemas(data: ConnectionData, encryptionKey : EncryptionKey): Unit = {
    try {
      registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
      try {
        val connection = new DriverManagerWithEncryption(encryptionKey).getConnection(data)
        val schemas = new MetadataSchemasLoader(connection.getMetaData, log).schemasNames()
        connection.close()
        guiActor ! ResponseSchemaExtraction(data, Some(SchemaNames(schemas)), None)
      } catch {
        case e: Throwable =>
          guiActor ! ResponseSchemaExtraction(data, None, Some(new Exception(localization.errorConnectingToDatabase(data.name) , e)))
      }
    } catch {
      case e: Throwable =>
        guiActor ! ResponseSchemaExtraction(data, None, Some(new Exception(localization.errorRegisteringDriver(data.name) , e)))
    }
  }
  /* closes all the database actors that serve the queries to a specific database */
  private def queryClose(databaseId : DatabaseId) : Unit = {
    log.debug("Closing the database "+DatabaseIdUtil.databaseIdText(databaseId))
    mapDBWorker.remove(databaseId).foreach(
      dbActor => dbActor ! QueryClose(databaseId) // routed to all dbWorkers of the router
      )
  }

  private def newConnections(datas: List[ConnectionData]) : Unit = {
    connectionsConfig = new ConnectionsConfig(datas)
    guiActor ! extractDatabaseIds()
  }

  private def newComposites(composites: List[Composite]): Unit = {
    currentComposites = mapComposites(composites)
    guiActor ! extractDatabaseIds()
  }

  private def extractDatabaseIds() = DatabaseIds(
      connectionsConfig.connections().map(c => SimpleDatabaseId(c)).map(id => DatabaseId(Left(id))) ++
        currentComposites.keys.map(id => DatabaseId(Right(id)))
    )

  def testConnection(data: ConnectionData, encryptionKey : EncryptionKey): Unit = {
    try {
      registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
      try {
        val connection = new DriverManagerWithEncryption(encryptionKey).getConnection(data)
        connection.close()
        guiActor ! ResponseTestConnection(data, None)
      } catch {
        case e: Throwable =>
          guiActor ! ResponseTestConnection(data, Some(new Exception(localization.errorConnectingToDatabase(data.name) , e)))
      }
    } catch {
      case e: Throwable =>
        guiActor ! ResponseTestConnection(data, Some(new Exception(localization.errorRegisteringDriver(data.name) , e)))
    }
  }

  def receive: PartialFunction[Any,Unit] = {
      case qry : QueryDatabase => queryDatabase(qry.databaseId, qry.encryptionKey)
      case qry : QueryClose => queryClose(qry.databaseId)
      case cpy : CopyToFile => startCopyWorker(cpy.databaseId, cpy.encryptionKey)
      case tst: TestConnection => testConnection(tst.data, tst.encryptionKey)
      case ext: ExtractSchemas => extractSchemas(ext.data, ext.encryptionKey)
      case datas: ConnectionDatas => newConnections(datas.datas)
      case composites: Composites => newComposites(composites.composites)
  }
}