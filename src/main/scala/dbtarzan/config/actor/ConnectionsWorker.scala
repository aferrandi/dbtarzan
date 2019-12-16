package dbtarzan.config.actor

import java.nio.file.Path

import akka.actor.{Actor, ActorRef}
import akka.routing.Broadcast
import dbtarzan.config.connections.{ConnectionData, ConnectionsConfig}
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.{ConnectionBuilder, DatabaseId, DriverManagerWithEncryption, DriverSpec, RegisterDriver}
import dbtarzan.localization.Localization
import dbtarzan.messages._

import scala.collection.mutable

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsWorker(datas : ConnectionDatas, guiActor : ActorRef, localization : Localization, 	keyFilesDirPath : Path) extends Actor {
	 private val mapDBWorker = mutable.HashMap.empty[DatabaseId, ActorRef]
	 private var connectionsConfig = new ConnectionsConfig(datas.datas)
	 private val log = new Logger(guiActor)

	 /* creates the actors to serve the queries for a database */
	 private def getDBWorker(databaseId : DatabaseId, encriptionKey : EncryptionKey) : ActorRef = {
    	val data = connectionsConfig.connect(databaseId.databaseName)
      val dbActor = ConnectionBuilder.buildDBWorker(data, encriptionKey, guiActor, context, localization, keyFilesDirPath)
      mapDBWorker += databaseId -> dbActor
      dbActor
	 } 

	/* creates the actor to serve the creation of foreign keys text files and start the copy */
	 private def startCopyWorker(databaseId : DatabaseId, encriptionKey : EncryptionKey) : Unit = {
    	val data = connectionsConfig.connect(databaseId.databaseName)
      val copyActor = ConnectionBuilder.buildCopyWorker(data, encriptionKey, guiActor, context, localization, keyFilesDirPath)
      copyActor ! CopyToFile
	 } 

	 /* if no actors are serving the queries to a specific database, creates them */
	 private def queryDatabase(databaseId : DatabaseId, encriptionKey : EncryptionKey) : Unit = {
	    	println("Querying the database "+databaseId.databaseName)
	    	try {
	    		if(!mapDBWorker.isDefinedAt(databaseId)) {
            val dbWorker = getDBWorker(databaseId, encriptionKey)
            guiActor ! ResponseDatabase(databaseId, dbWorker)
				  } else
	    			guiActor ! ErrorDatabaseAlreadyOpen(databaseId)
			} catch {
				case e : Exception => {
					log.error(localization.errorQueryingDatabase(databaseId.databaseName), e)
					e.printStackTrace()
				}	    	
			}	 	
	 }

	 /* closes all the database actors that serve the queries to a specific database */
	 private def queryClose(databaseId : DatabaseId) : Unit = {
	    println("Closing the database "+databaseId.databaseName) 
      mapDBWorker.remove(databaseId).foreach(
        dbActor => dbActor ! Broadcast(QueryClose(databaseId)) // routed to all dbWorkers of the router
        )
	 }

	 private def newConnections(datas: ConnectionDatas) : Unit = {
      connectionsConfig = new ConnectionsConfig(datas.datas)
      guiActor ! DatabaseIds(connectionsConfig.connections().map(DatabaseId))
	 }

  def testConnection(data: ConnectionData, encryptionKey : EncryptionKey): Unit = try {
    println("Testing "+data)
      RegisterDriver.registerDriver(DriverSpec(data.jar, data.driver))
      new DriverManagerWithEncryption(encryptionKey).getConnection(data)
      guiActor ! ResponseTestConnection(data, None)
  } catch {
    case e: Exception => {
      guiActor ! ResponseTestConnection(data, Some(e))
    }
  }

  def receive = {
      case qry : QueryDatabase => queryDatabase(qry.databaseId, qry.encryptionKey)
	    case qry : QueryClose => queryClose(qry.databaseId)
	    case cpy : CopyToFile => startCopyWorker(cpy.databaseId, cpy.encryptionKey)
      case tst: TestConnection => testConnection(tst.data, tst.encryptionKey)
	    case datas: ConnectionDatas => newConnections(datas)
	}
}