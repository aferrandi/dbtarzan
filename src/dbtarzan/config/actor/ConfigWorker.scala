package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import dbtarzan.messages._
import dbtarzan.config.Config
import dbtarzan.db.ConnectionBuilder
import scala.collection.mutable.HashMap

class ConfigWorker(config : Config, guiActor : ActorRef) extends Actor {
	 private val mapDatabase = HashMap.empty[String, ActorRef]

	 private def getDatabase(databaseName : String) : ActorRef = {
	    	val data = config.connect(databaseName)
    		val dbActor = ConnectionBuilder.build(data, guiActor)
    		mapDatabase += databaseName -> dbActor
    		dbActor
	 } 

	 def receive = {
	    case qry : QueryDatabase => {
	    	println("Querying the database "+qry.databaseName)
	    	try {
	    		if(!mapDatabase.isDefinedAt(qry.databaseName))
	    			guiActor ! ResponseDatabase(qry.databaseName, getDatabase(qry.databaseName))
	    		else
	    			guiActor ! ErrorDatabaseAlreadyOpen(qry.databaseName)
			} catch {
				case e : Exception => guiActor ! Error(e)	    	
			}

    	}
	}
}