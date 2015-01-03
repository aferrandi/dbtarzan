package dbtarzan.config

import java.sql.{DriverManager, Driver}
import scala.util.{Try, Success, Failure}
import java.net.{ URL, URLClassLoader }
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSystem
import dbtarzan.db.actor.DatabaseWorker

class Config(connectionDatas : List[ConnectionData]) {
	val system = ActorSystem("Sys")
	val connectionDatasByName = connectionDatas.groupBy(data => data.name)

	def connect(name : String, guiActor : ActorRef) : Try[ActorRef] = 
		connectionDatasByName.get(name).map(datasPerName => 
			if(datasPerName.size == 1) 
				Success(buildConnection(datasPerName.head, guiActor))
			else
				Failure(new Exception("Multiple connections with the name "+name))
		).getOrElse(Failure(new Exception("No connection with the name "+name)))

	
	private def buildConnection(data : ConnectionData, guiActor : ActorRef) : ActorRef = {
		// Load the driver
		val url = new URL("jar:file:"+data.jar+"!/");
		val classLoader = new URLClassLoader(Array(url))
		val driver = Class.forName(data.driver, true, classLoader).newInstance().asInstanceOf[Driver]
		DriverManager.registerDriver(new dbtarzan.db.DriverShim(driver));		
		system.actorOf(Props(new DatabaseWorker(data, guiActor)).withDispatcher("my-pinned-dispatcher"), "dbworker"+data.name)
	}

	def connections() = connectionDatasByName.keys.toList
}