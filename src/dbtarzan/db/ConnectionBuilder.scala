package dbtarzan.db

import akka.actor.{ ActorRef, Props, ActorContext }
import dbtarzan.db.actor.{ DatabaseWorker, CopyWorker }
import akka.routing.{ RoundRobinPool}
import dbtarzan.config.connections.ConnectionData
import java.sql.{DriverManager, Driver}
import java.net.{ URL, URLClassLoader }

private class ConnectionBuilder(data : ConnectionData, guiActor : ActorRef, context : ActorContext) {	
	def buildDBWorker() : ActorRef = try {
		registerDriver()		
		val instances = data.instances.getOrElse(1)
		context.actorOf(RoundRobinPool(instances).props(buildSubWorkerProps()))
	} catch {
		case c: ClassNotFoundException => throw new Exception("Building the dbWorker with the driver "+data.driver+" got ClassNotFoundException:",c)
		case t: Throwable => throw new Exception("Building the dbWorker with the driver "+data.driver+" got the exception of type "+t.getClass().getCanonicalName()+":",t) 
	}

	def buildCopyWorker() : ActorRef = try {
		registerDriver()
		val name = "copyworker" + data.name		
		context.actorOf(Props(new CopyWorker(data, guiActor)).withDispatcher("my-pinned-dispatcher"), name)
	} catch { 
		case c: ClassNotFoundException => throw new Exception("Getting the copyworker with the driver "+data.driver+" got ClassNotFoundException:",c)
		case t: Throwable => throw new Exception("Getting the copyworker with the driver "+data.driver+" got the exception of type "+t.getClass().getCanonicalName()+":",t) 
	}

	private def registerDriver() : Unit = {
		// Load the driver
		val url = new URL("jar:file:"+data.jar+"!/")
		val classLoader = new URLClassLoader(Array(url))
		val driverClass = Class.forName(data.driver, true, classLoader)
		val driver = driverClass.newInstance().asInstanceOf[Driver]
		DriverManager.registerDriver(new DriverShim(driver))		
	} 

	private def buildSubWorkerProps() : Props = {
		Props(classOf[DatabaseWorker], DriverManagerWithEncryption, data, guiActor).withDispatcher("my-pinned-dispatcher") 
	}	

	private def buildSubWorkerName(index : Int) : String = {
		"dbworker" + data.name + index
	}	

}

object ConnectionBuilder {
	def buildDBWorker(data : ConnectionData, guiActor : ActorRef, context : ActorContext) : ActorRef = 
		new ConnectionBuilder(data, guiActor, context).buildDBWorker()

	def buildCopyWorker(data : ConnectionData, guiActor : ActorRef, context : ActorContext) : ActorRef = 
		new ConnectionBuilder(data, guiActor, context).buildCopyWorker()
}
