package dbtarzan.config.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.*
import dbtarzan.db.basicmetadata.MetadataSchemasLoader
import dbtarzan.localization.Localization
import dbtarzan.messages.*

import java.nio.file.Path
import scala.collection.mutable

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsActor(connectionsDatas : List[ConnectionData],
                       composites: List[Composite],
                       guiActor : ActorRef,
                       localization : Localization,
                       keyFilesDirPath : Path) extends Actor {
  private val mapDBWorker = mutable.HashMap.empty[DatabaseId, ActorRef]
  private var connectionsDataMap = new ConnectionsDataMap(connectionsDatas)
  private var currentComposites : Map[CompositeId, Composite] = mapComposites(composites)

  private def mapComposites(composites: List[Composite]): Map[CompositeId, Composite] =
    composites.map(composite => composite.compositeId -> composite).toMap

  private val registerDriver = new RegisterDriver()

  private val log = new Logger(guiActor)

  private def datasFromDatabaseId(databaseId: DatabaseId): Option[List[ConnectionData]] = {
    databaseId.origin match {
      case Left(simpleDatabaseId) => Some(List(connectionsDataMap.connectionDataFor(simpleDatabaseId)))
      case Right(compositeId) => currentComposites.get(compositeId).map(
        composite => composite.databaseIds.map(simpleDatabaseId => connectionsDataMap.connectionDataFor(simpleDatabaseId))
      )
    }
  }

   /* creates the actors to serve the queries for a database */
  private def getDBActor(databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : ActorRef = {
   datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val dbActor = ConnectionBuilder.buildDBActor(databaseId, registerDriver, datas, encriptionKey, guiActor, context, localization, keyFilesDirPath, loginPasswords)
         mapDBWorker += databaseId -> dbActor
         dbActor
       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
     }
  }

  /* creates the actor to serve the creation of foreign keys text files and start the copy */
  private def startCopyWorker(databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : Unit =
   datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val copyActor = ConnectionBuilder.buildCopyWorker(databaseId, registerDriver, datas, encriptionKey, guiActor, context, localization, keyFilesDirPath, loginPasswords)
         copyActor ! CopyToFile
       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
   }

  /* if no actors are serving the queries to a specific database, creates them */
  private def queryDatabase(databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : Unit = {
      log.debug("Querying the tables of the database "+DatabaseIdUtil.databaseIdText(databaseId))
      try {
        if(!mapDBWorker.isDefinedAt(databaseId)) {
          val dbWorker = getDBActor(databaseId, encriptionKey, loginPasswords)
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


  /* closes all the database actors that serve the queries to a specific database */
  private def queryClose(databaseId : DatabaseId) : Unit = {
    log.debug("Closing the database "+DatabaseIdUtil.databaseIdText(databaseId))
    mapDBWorker.remove(databaseId).foreach(
      dbActor => dbActor ! QueryClose(databaseId) // routed to all dbWorkers of the router
      )
  }

  private def newConnections(datas: List[ConnectionData]) : Unit = {
    connectionsDataMap = new ConnectionsDataMap(datas)
    guiActor ! extractDatabaseInfos()
  }

  private def newComposites(composites: List[Composite]): Unit = {
    currentComposites = mapComposites(composites)
    guiActor ! extractDatabaseInfos()
  }

  private def extractDatabaseInfos(): DatabaseInfos = {
    val connectionsDataRemaining = connectionsNotInComposites()
    val connectionInfos = DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionsDataRemaining)
    val compositeInfos = DatabaseInfoFromConfig.extractCompositeInfos(currentComposites.values.toList, connectionsDataMap.connectionDataFor)
    DatabaseInfos(connectionInfos ++ compositeInfos)
  }

  private def connectionsNotInComposites(): List[ConnectionData] = {
    val connectionsToRemove = currentComposites.values.filter(co => !co.showAlsoIndividualDatabases).flatMap(co => co.databaseIds).map(id => id.databaseName).toSet
    val connectionsDataRemaining = connectionsDataMap.connectionDatas.filter(cd => !connectionsToRemove.contains(cd.name))
    connectionsDataRemaining
  }

  private def testConnection(data: ConnectionData, encryptionKey : EncryptionKey, loginPassword: Option[Password]): Unit = {
    val connectionCore = new ConnectionCore(registerDriver, log, localization)
    guiActor ! connectionCore.testConnection(data, encryptionKey, loginPassword)
  }

  private def extractSchemas(data: ConnectionData, encryptionKey: EncryptionKey, loginPassword: Option[Password]): Unit = {
    val connectionCore = new ConnectionCore(registerDriver, log, localization)
    guiActor ! connectionCore.extractSchemas(data, encryptionKey, loginPassword)
  }

  def receive: PartialFunction[Any,Unit] = {
      case qry : QueryDatabase => queryDatabase(qry.databaseId, qry.encryptionKey, qry.loginPasswords)
      case qry : QueryClose => queryClose(qry.databaseId)
      case cpy : CopyToFile => startCopyWorker(cpy.databaseId, cpy.encryptionKey, cpy.loginPasswords)
      case tst: TestConnection => testConnection(tst.data, tst.encryptionKey, tst.loginPassword)
      case ext: ExtractSchemas => extractSchemas(ext.data, ext.encryptionKey, ext.loginPassword)
      case datas: ConnectionDatas => newConnections(datas.datas)
      case composites: Composites => newComposites(composites.composites)
  }
}