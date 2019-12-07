package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Broadcast
import scala.collection.mutable.HashMap
import java.nio.file.Path

import dbtarzan.messages._
import dbtarzan.config.connections.ConnectionsConfig
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.{ConnectionBuilder, DatabaseId }
import dbtarzan.localization.Localization

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsWorker(datas : ConnectionDatas, guiActor : ActorRef, localization : Localization, 	keyFilesDirPath : Path) extends Actor {
	 private val mapDBWorker = HashMap.empty[DatabaseId, ActorRef]
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
				}
	    		else
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
	 	guiActor ! DatabaseIds(connectionsConfig.connections.map(DatabaseId(_)))
	 }

	 def receive = {
	    case qry : QueryDatabase => queryDatabase(qry.databaseId, qry.encryptionKey)
	    case qry : QueryClose => queryClose(qry.databaseId)
	    case cpy : CopyToFile => startCopyWorker(cpy.databaseId, cpy.encryptionKey)
	    case datas: ConnectionDatas => newConnections(datas)
	}
}