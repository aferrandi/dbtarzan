package dbtarzan.db

import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSystem
import dbtarzan.db.actor.DatabaseWorker
import akka.routing.RoundRobinRouter
import dbtarzan.config.ConnectionData
import java.sql.{DriverManager, Driver}
import java.net.{ URL, URLClassLoader }

/**
	builds database actors with connections created on the basis of a block in the configuration file.
*/
class ConnectionBuilder(data : ConnectionData, guiActor : ActorRef) {
	val system = ActorSystem("Sys")
	def buildConnection() : ActorRef = {
		try {
			registerDriver()		
			val range = 1 to data.instances.getOrElse(1)
			val actorRefs = range.map(index => buildWorker(index))
			system.actorOf(Props.empty.withRouter(RoundRobinRouter(routees = actorRefs)))
		} catch { 
			case t: Throwable => throw new Exception("Getting the driver "+data.driver+" got:"+t)
		}

	}

	private def registerDriver() : Unit = {
		// Load the driver
		val url = new URL("jar:file:"+data.jar+"!/")
		val classLoader = new URLClassLoader(Array(url))
		val driverClass = Class.forName(data.driver, true, classLoader)
		val driver = driverClass.newInstance().asInstanceOf[Driver]
		DriverManager.registerDriver(new DriverShim(driver))		
	} 

	private def buildWorker(index : Int) = {
		val name = "dbworker" + data.name + index
		system.actorOf(Props(new DatabaseWorker(data, guiActor)).withDispatcher("my-pinned-dispatcher"), name) 
	}	
}

object ConnectionBuilder {
	def build(data : ConnectionData, guiActor : ActorRef) : ActorRef = 
		new ConnectionBuilder(data, guiActor).buildConnection()
}
