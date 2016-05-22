package dbtarzan.db

import akka.actor.{ ActorRef, Props, ActorContext }
import dbtarzan.db.actor.{ DatabaseWorker, CopyWorker }
import akka.routing.RoundRobinRouter
import dbtarzan.config.ConnectionData
import java.sql.{DriverManager, Driver}
import java.net.{ URL, URLClassLoader }

/**
	builds database actors with connections created on the basis of a block in the configuration file.
*/
private class ConnectionBuilder(data : ConnectionData, guiActor : ActorRef, context : ActorContext) {	
	def buildDBWorker() : ActorRef = try {
		registerDriver()		
		val range = 1 to data.instances.getOrElse(1)
		val actorRefs = range.map(index => buildSubWorker(index))
		context.actorOf(Props.empty.withRouter(RoundRobinRouter(routees = actorRefs)))
	} catch { 
		case t: Throwable => throw new Exception("Building the dbWorker with the driver "+data.driver+" got:",t)
	}

	def buildCopyWorker() : ActorRef = try {
		registerDriver()
		val name = "copyworker" + data.name		
		context.actorOf(Props(new CopyWorker(data, guiActor)).withDispatcher("my-pinned-dispatcher"), name)
	} catch { 
		case t: Throwable => throw new Exception("Getting the copyworker with the driver "+data.driver+" got:",t)
	}

	private def registerDriver() : Unit = {
		// Load the driver
		val url = new URL("jar:file:"+data.jar+"!/")
		val classLoader = new URLClassLoader(Array(url))
		val driverClass = Class.forName(data.driver, true, classLoader)
		val driver = driverClass.newInstance().asInstanceOf[Driver]
		DriverManager.registerDriver(new DriverShim(driver))		
	} 

	private def buildSubWorker(index : Int) : ActorRef = {
		val name = "dbworker" + data.name + index
		val connection = DriverManager.getConnection(data.url, data.user, data.password)
		context.actorOf(Props(new DatabaseWorker(connection, data, guiActor)).withDispatcher("my-pinned-dispatcher"), name) 
	}	
}

object ConnectionBuilder {
	def buildDBWorker(data : ConnectionData, guiActor : ActorRef, context : ActorContext) : ActorRef = 
		new ConnectionBuilder(data, guiActor, context).buildDBWorker()

	def buildCopyWorker(data : ConnectionData, guiActor : ActorRef, context : ActorContext) : ActorRef = 
		new ConnectionBuilder(data, guiActor, context).buildCopyWorker()
}
