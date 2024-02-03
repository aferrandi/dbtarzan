package dbtarzan.config.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.*
import dbtarzan.db.basicmetadata.MetadataSchemasLoader
import dbtarzan.gui.actor.GUIActor
import dbtarzan.localization.Localization
import dbtarzan.log.actor.{LogActor, Logger}
import dbtarzan.messages.*

import java.nio.file.Path
import scala.collection.mutable

case class ConnectionsInitData(guiActor: ActorRef, logActor: ActorRef)

case class ConnectionsInitState(guiActor: ActorRef, log: Logger)

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsActor(connectionsDatas : List[ConnectionData],
                       composites: List[Composite],
                       localization : Localization,
                       keyFilesDirPath : Path
                      ) extends Actor {
  private val mapDBWorker = mutable.HashMap.empty[DatabaseId, ActorRef]
  private var connectionsDataMap = new ConnectionsDataMap(connectionsDatas)
  private var currentComposites : Map[CompositeId, Composite] = mapComposites(composites)

  private def mapComposites(composites: List[Composite]): Map[CompositeId, Composite] =
    composites.map(composite => composite.compositeId -> composite).toMap

  private val registerDriver = new RegisterDriver()

  private var initState: Option[ConnectionsInitState] = None

  private def datasFromDatabaseId(databaseId: DatabaseId): Option[List[ConnectionData]] =
    databaseId.origin match {
      case Left(simpleDatabaseId) => Some(List(connectionsDataMap.connectionDataFor(simpleDatabaseId)))
      case Right(compositeId) => currentComposites.get(compositeId).map(
        composite => composite.databaseIds.map(simpleDatabaseId => connectionsDataMap.connectionDataFor(simpleDatabaseId))
      )
    }

   /* creates the actors to serve the queries for a database */
  private def getDBActor(state: ConnectionsInitState, databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : ActorRef =
    datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val dbActor = ConnectionBuilder.buildDBActor(databaseId, registerDriver, datas, encriptionKey, state.guiActor, state.log, context, localization, keyFilesDirPath, loginPasswords)
         mapDBWorker += databaseId -> dbActor
         dbActor

       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
     }


  /* creates the actor to serve the creation of foreign keys text files and start the copy */
  private def startCopyWorker(state: ConnectionsInitState, databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : Unit =
   datasFromDatabaseId(databaseId) match {
       case Some(datas) => {
         val copyActor = ConnectionBuilder.buildCopyWorker(databaseId, registerDriver, datas, encriptionKey, state.guiActor, state.log, context, localization, keyFilesDirPath, loginPasswords)
         copyActor ! CopyToFile
       }
       case None => throw new Exception(s"No datas found for ${databaseId}")
   }

  /* if no actors are serving the queries to a specific database, creates them */
  private def queryDatabase(state: ConnectionsInitState, databaseId : DatabaseId, encriptionKey : EncryptionKey, loginPasswords: LoginPasswords) : Unit = {
      state.log.debug("Querying the tables of the database "+DatabaseIdUtil.databaseIdText(databaseId))
      try {
        if(!mapDBWorker.isDefinedAt(databaseId)) {
          val dbWorker = getDBActor(state, databaseId, encriptionKey, loginPasswords)
          dbWorker ! QueryTables(databaseId, dbWorker)
        } else
          state.guiActor ! ErrorDatabaseAlreadyOpen(databaseId)
    } catch {
      case e : Exception => {
        state.log.error(localization.errorQueryingDatabase(DatabaseIdUtil.databaseIdText(databaseId)), e)
        e.printStackTrace()
      }
    }
  }

  /* closes all the database actors that serve the queries to a specific database */
  private def queryClose(state: ConnectionsInitState, databaseId : DatabaseId) : Unit = {
    state.log.debug("Closing the database "+DatabaseIdUtil.databaseIdText(databaseId))
    mapDBWorker.remove(databaseId).foreach(
      dbActor => dbActor ! QueryClose(databaseId) // routed to all dbWorkers of the router
      )
  }

  private def newConnections(state: ConnectionsInitState, datas: List[ConnectionData]) : Unit = {
    connectionsDataMap = new ConnectionsDataMap(datas)
    sendDatabaseInfos(state)
  }

  private def newComposites(state: ConnectionsInitState, composites: List[Composite]): Unit = {
    currentComposites = mapComposites(composites)
    sendDatabaseInfos(state)
  }

  private def sendDatabaseInfos(state: ConnectionsInitState): Unit =
    state.guiActor ! DatabaseInfoExtractor.extractDatabaseInfos(currentComposites.values.toList, connectionsDataMap)

  private def testConnection(state: ConnectionsInitState, data: ConnectionData, encryptionKey : EncryptionKey, loginPassword: Option[Password]): Unit = {
      val connectionCore = new ConnectionCore(registerDriver, state.log, localization)
      state.guiActor ! connectionCore.testConnection(data, encryptionKey, loginPassword)
  }

  private def extractSchemas(state: ConnectionsInitState, data: ConnectionData, encryptionKey: EncryptionKey, loginPassword: Option[Password]): Unit = {
      val connectionCore = new ConnectionCore(registerDriver, state.log, localization)
      state.guiActor ! connectionCore.extractSchemas(data, encryptionKey, loginPassword)
  }

  def intiialized: Receive = {
      case qry : QueryDatabase => initState.foreach(state => queryDatabase(state, qry.databaseId, qry.encryptionKey, qry.loginPasswords))
      case qry : QueryClose => initState.foreach(state => queryClose(state, qry.databaseId))
      case cpy : CopyToFile => initState.foreach(state => startCopyWorker(state, cpy.databaseId, cpy.encryptionKey, cpy.loginPasswords))
      case tst: TestConnection => initState.foreach(state => testConnection(state, tst.data, tst.encryptionKey, tst.loginPassword))
      case ext: ExtractSchemas => initState.foreach(state => extractSchemas(state, ext.data, ext.encryptionKey, ext.loginPassword))
      case datas: ConnectionDatas => initState.foreach(state => newConnections(state, datas.datas))
      case composites: Composites => initState.foreach(state => newComposites(state, composites.composites))
  }

  def receive: PartialFunction[Any, Unit] = {
    case initData : ConnectionsInitData  => {
      this.initState = Some(new ConnectionsInitState(initData.guiActor, new Logger(initData.logActor)))
      context.become(intiialized)
    }
  }
}