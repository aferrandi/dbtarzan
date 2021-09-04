package dbtarzan.db

import java.nio.file.Path

import akka.actor.{ActorContext, ActorRef, Props}
import akka.routing.RoundRobinPool
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.actor.{CopyActor, DatabaseActor}
import dbtarzan.localization.Localization

private class ConnectionBuilder(registerDriver: RegisterDriver, data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) {
	def buildDBWorker() : ActorRef = try {
    registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
		val instances = data.instances.getOrElse(1)
    connectionContext.actorOf(RoundRobinPool(instances).props(buildSubWorkerProps()))
	} catch {
		case c: ClassNotFoundException => throw new Exception("Building the dbWorker with the driver "+data.driver+" got ClassNotFoundException:",c)
		case t: Throwable => throw new Exception("Building the dbWorker with the driver "+data.driver+" got the exception of type "+t.getClass.getCanonicalName+":",t)
	}

	def buildCopyWorker() : ActorRef = try {
    registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
		val name = "copyworker" + data.name
    connectionContext.actorOf(Props(new CopyActor(data, encriptionKey, guiActor, localization, keyFilesDirPath)).withDispatcher("my-pinned-dispatcher"), name)
	} catch { 
		case c: ClassNotFoundException => throw new Exception("Getting the copyworker with the driver "+data.driver+" got ClassNotFoundException:",c)
		case t: Throwable => throw new Exception("Getting the copyworker with the driver "+data.driver+" got the exception of type "+t.getClass.getCanonicalName+":",t)
	}

	private def buildSubWorkerProps() : Props = {
		Props(classOf[DatabaseActor], encriptionKey, data, guiActor, connectionContext.self, localization, keyFilesDirPath).withDispatcher("my-pinned-dispatcher")
	}	

	private def buildSubWorkerName(index : Int) : String = {
		"dbworker" + data.name + index
	}
}

object ConnectionBuilder {
	def buildDBWorker(registerDriver: RegisterDriver, data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = {
    val builder = new ConnectionBuilder(registerDriver, data, encriptionKey, guiActor, connectionContext, localization, keyFilesDirPath)
    builder.buildDBWorker()
  }

	def buildCopyWorker(registerDriver: RegisterDriver, data : ConnectionData, encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = {
    val builder = new ConnectionBuilder(registerDriver, data, encriptionKey, guiActor, connectionContext, localization, keyFilesDirPath)
    builder.buildCopyWorker()
  }
}
