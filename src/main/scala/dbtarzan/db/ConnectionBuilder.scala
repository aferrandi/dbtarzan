package dbtarzan.db

import akka.actor.{ ActorRef, Props, ActorContext }
import akka.routing.{ RoundRobinPool}
import java.sql.{DriverManager, Driver}
import java.net.{ URL, URLClassLoader }
import java.nio.file.Path

import dbtarzan.db.actor.{ DatabaseWorker, CopyWorker }
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.localization.Localization

private class ConnectionBuilder(data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, context : ActorContext, localization : Localization, keyFilesDirPath: Path) {	
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
		context.actorOf(Props(new CopyWorker(data, encriptionKey, guiActor, localization, keyFilesDirPath)).withDispatcher("my-pinned-dispatcher"), name)
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
		Props(classOf[DatabaseWorker], encriptionKey, data, guiActor, localization, keyFilesDirPath).withDispatcher("my-pinned-dispatcher") 
	}	

	private def buildSubWorkerName(index : Int) : String = {
		"dbworker" + data.name + index
	}	

}

object ConnectionBuilder {
	def buildDBWorker(data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, context : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = 
		new ConnectionBuilder(data, encriptionKey, guiActor, context, localization, keyFilesDirPath).buildDBWorker()

	def buildCopyWorker(data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, context : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = 
		new ConnectionBuilder(data, encriptionKey, guiActor, context, localization, keyFilesDirPath).buildCopyWorker()
}