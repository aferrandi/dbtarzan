package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import dbtarzan.messages._
import dbtarzan.config.Config
import dbtarzan.db.ConnectionBuilder

class ConfigWorker(config : Config, guiActor : ActorRef) extends Actor {
	 def receive = {
	    case qry : QueryDatabase => {
	    	println("Querying the database "+qry.databaseName)
	    	try {
		    	val data = config.connect(qry.databaseName)
	    		val dbActor = ConnectionBuilder.build(data, guiActor)
			    guiActor ! ResponseDatabase(qry.databaseName, dbActor)
			} catch {
				case e : Exception => guiActor ! Error(e)
	    	}
    	}
	}
}