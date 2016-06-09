package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Broadcast
import dbtarzan.messages._
import dbtarzan.config.Config
import dbtarzan.db.ConnectionBuilder
import scala.collection.mutable.HashMap

/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class ConfigWorker(datas : ConnectionDatas, guiActor : ActorRef) extends Actor {
	 private val mapDBWorker = HashMap.empty[String, ActorRef]
	 private var config = new Config(datas.datas)

	 /* creates the actors to serve the queries for a database */
	 private def getDBWorker(databaseName : String) : ActorRef = {
    	val data = config.connect(databaseName)
		val dbActor = ConnectionBuilder.buildDBWorker(data, guiActor, context)
		mapDBWorker += databaseName -> dbActor
		dbActor
	 } 

	/* creates the actor to serve the creation of foreign keys text files and start the copy */
	 private def startCopyWorker(databaseName : String) : Unit = {
    	val data = config.connect(databaseName)
		val copyActor = ConnectionBuilder.buildCopyWorker(data, guiActor, context)
		copyActor ! CopyToFile
	 } 

	 /* if no actors are serving the queries to a specific database, creates them */
	 private def queryDatabase(databaseName : String) : Unit = {
	    	println("Querying the database "+databaseName)
	    	try {
	    		if(!mapDBWorker.isDefinedAt(databaseName))
	    			guiActor ! ResponseDatabase(databaseName, getDBWorker(databaseName))
	    		else
	    			guiActor ! ErrorDatabaseAlreadyOpen(databaseName)
			} catch {
				case e : Exception => {
					guiActor ! Error("Querying the database "+databaseName+" got", e)
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
	 	config = new Config(datas.datas)
	 	guiActor ! DatabaseNames(config.connections)
	 }

	 def receive = {
	    case qry : QueryDatabase => queryDatabase(qry.databaseName)
	    case qry : QueryClose => queryClose(qry.databaseName)
	    case cpy : CopyToFile => startCopyWorker(cpy.databaseName)
	    case datas: ConnectionDatas => newConnections(datas)
	}
}