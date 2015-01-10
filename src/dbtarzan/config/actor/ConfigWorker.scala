package dbtarzan.config.actor

import akka.actor.Actor
import akka.actor.ActorRef
import dbtarzan.messages._
import dbtarzan.config.Config
import dbtarzan.db.ConnectionBuilder

class ConfigWorker(config : Config, guiActor : ActorRef) extends Actor {
	 def receive = {
	    case qry : QueryDatabase =>    {
	    	val optData = config.connect(qry.databaseName)      
    		val optDbActor = optData.map(data => ConnectionBuilder.build(data, guiActor))
		    optDbActor.foreach(dbActor => guiActor ! ResponseDatabase(qry.databaseName, dbActor))
    	}
	}
}