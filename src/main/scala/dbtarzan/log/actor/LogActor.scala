package dbtarzan.log.actor

import org.apache.pekko.actor.{Actor, ActorRef}
import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.config.password.{EncryptionKey, Password}
import dbtarzan.db.*
import dbtarzan.db.basicmetadata.MetadataSchemasLoader
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.localization.Localization
import dbtarzan.messages.*

import java.io.FileWriter
import java.nio.file.Path
import scala.collection.mutable

case class LogInitData(guiActor: ActorRef)



/* an actor that uses the database configuration to start database actors, acting as a database actors factory */
class LogActor(
                
              ) extends Actor {
  val fileLogger = new FileLogger()
  var guiActor : Option[ActorRef] = None

  def intiialized: Receive = {
    case msg: TLogMessage => guiActor.foreach(ga => log(ga, msg))
  }

  def log(guiActor: ActorRef, msg: TLogMessage): Unit = {
    if (!msg.isInstanceOf[Debug])
      guiActor ! msg
    fileLogger.log(msg)
  }
  def receive: PartialFunction[Any,Unit] = {
    case initData: LogInitData => {
      this.guiActor = Some(initData.guiActor)
      context.become(intiialized)
    }
  }
}