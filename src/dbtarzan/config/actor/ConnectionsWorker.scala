package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Broadcast
import scala.collection.mutable.HashMap

import dbtarzan.messages._
import dbtarzan.config.ConnectionsConfig
import dbtarzan.db.ConnectionBuilder

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConnectionsWorker(datas : ConnectionDatas, guiActor : ActorRef) extends Actor {
	 private val mapDBWorker = HashMap.empty[String, ActorRef]
	 private var connectionsConfig = new ConnectionsConfig(datas.datas)
	 private val log = new Logger(guiActor)

	 /* creates the actors to serve the queries for a database */
	 private def getDBWorker(databaseName : String) : ActorRef = {
    	val data = connectionsConfig.connect(databaseName)
		val dbActor = ConnectionBuilder.buildDBWorker(data, guiActor, context)
		mapDBWorker += databaseName -> dbActor
		dbActor
	 } 

	/* creates the actor to serve the creation of foreign keys text files and start the copy */
	 private def startCopyWorker(databaseName : String) : Unit = {
    	val data = connectionsConfig.connect(databaseName)
		val copyActor = ConnectionBuilder.buildCopyWorker(data, guiActor, context)
		copyActor ! CopyToFile
	 } 

	 /* if no actors are serving the queries to a specific database, creates them */
	 private def queryDatabase(databaseName : String) : Unit = {
	    	println("Querying the database "+databaseName)
	    	try {
	    		if(!mapDBWorker.isDefinedAt(databaseName)) {
					val dbWorker = getDBWorker(databaseName)
	    			guiActor ! ResponseDatabase(databaseName, dbWorker)
				}
	    		else
	    			guiActor ! ErrorDatabaseAlreadyOpen(databaseName)
			} catch {
				case e : Exception => {
					log.error("Querying the database "+databaseName+" got", e)
					e.printStackTrace()
				}	    	
			}	 	
	 }

	 /* closes all the database actors that serve the queries to a specific database */
	 private def queryClose(databaseName : String) : Unit = {
	    println("Closing the database "+databaseName) 
	 	mapDBWorker.remove(databaseName).foreach(
	 		dbActor => dbActor ! Broadcast(QueryClose(databaseName)) // routed to all dbWorkers of the router
	 		)
	 }

	 private def newConnections(datas: ConnectionDatas) : Unit =
	 {
	 	connectionsConfig = new ConnectionsConfig(datas.datas)
	 	guiActor ! DatabaseNames(connectionsConfig.connections)
	 }

	 def receive = {
	    case qry : QueryDatabase => queryDatabase(qry.databaseName)
	    case qry : QueryClose => queryClose(qry.databaseName)
	    case cpy : CopyToFile => startCopyWorker(cpy.databaseName)
	    case datas: ConnectionDatas => newConnections(datas)
	}
}