package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Broadcast
import scala.collection.mutable.HashMap

import dbtarzan.messages._
import dbtarzan.config.ConnectionsConfig
import dbtarzan.db.{ConnectionBuilder, DatabaseId }

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsWorker(datas : ConnectionDatas, guiActor : ActorRef) extends Actor {
	 private val mapDBWorker = HashMap.empty[DatabaseId, ActorRef]
	 private var connectionsConfig = new ConnectionsConfig(datas.datas)
	 private val log = new Logger(guiActor)

	 /* creates the actors to serve the queries for a database */
	 private def getDBWorker(databaseId : DatabaseId) : ActorRef = {
    	val data = connectionsConfig.connect(databaseId.databaseName)
		val dbActor = ConnectionBuilder.buildDBWorker(data, guiActor, context)
		mapDBWorker += databaseId -> dbActor
		dbActor
	 } 

	/* creates the actor to serve the creation of foreign keys text files and start the copy */
	 private def startCopyWorker(databaseId : DatabaseId) : Unit = {
    	val data = connectionsConfig.connect(databaseId.databaseName)
		val copyActor = ConnectionBuilder.buildCopyWorker(data, guiActor, context)
		copyActor ! CopyToFile
	 } 

	 /* if no actors are serving the queries to a specific database, creates them */
	 private def queryDatabase(databaseId : DatabaseId) : Unit = {
	    	println("Querying the database "+databaseId.databaseName)
	    	try {
	    		if(!mapDBWorker.isDefinedAt(databaseId)) {
					val dbWorker = getDBWorker(databaseId)
	    			guiActor ! ResponseDatabase(databaseId, dbWorker)
				}
	    		else
	    			guiActor ! ErrorDatabaseAlreadyOpen(databaseId)
			} catch {
				case e : Exception => {
					log.error("Querying the database "+databaseId.databaseName+" got", e)
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
	    case qry : QueryDatabase => queryDatabase(qry.databaseId)
	    case qry : QueryClose => queryClose(qry.databaseId)
	    case cpy : CopyToFile => startCopyWorker(cpy.databaseId)
	    case datas: ConnectionDatas => newConnections(datas)
	}
}