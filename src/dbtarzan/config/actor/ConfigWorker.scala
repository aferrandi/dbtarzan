package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.routing.Broadcast
import dbtarzan.messages._
import dbtarzan.config.Config
import dbtarzan.db.ConnectionBuilder
import scala.collection.mutable.HashMap

class ConfigWorker(config : Config, guiActor : ActorRef) extends Actor {
	 private val mapDatabase = HashMap.empty[String, ActorRef]

	 private def getDatabase(databaseName : String) : ActorRef = {
	    	val data = config.connect(databaseName)
    		val dbActor = ConnectionBuilder.build(data, guiActor, context)
    		mapDatabase += databaseName -> dbActor
    		dbActor
	 } 

	 private def queryDatabase(databaseName : String) : Unit = {
	    	println("Querying the database "+databaseName)
	    	try {
	    		if(!mapDatabase.isDefinedAt(databaseName))
	    			guiActor ! ResponseDatabase(databaseName, getDatabase(databaseName))
	    		else
	    			guiActor ! ErrorDatabaseAlreadyOpen(databaseName)
			} catch {
				case e : Exception => guiActor ! Error(e)	    	
			}	 	
	 }

	 private def queryClose(databaseName : String) : Unit = {
	    println("Closing the database "+databaseName) 
	 	mapDatabase.remove(databaseName).foreach(
	 		dbActor => dbActor ! Broadcast(QueryClose(databaseName)) // routed to all dbWorkers of the router
	 		)
	 }

	 def receive = {
	    case qry : QueryDatabase => queryDatabase(qry.databaseName)
	    case qry : QueryClose => queryClose(qry.databaseName)
	}
}