package dbtarzan.db

import org.apache.pekko.actor.{ActorContext, ActorRef, Props}
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.actor.{CopyActor, DatabaseActor}
import dbtarzan.localization.Localization
import dbtarzan.messages.DatabaseIdUtil

import java.nio.file.Path
private class ConnectionBuilder(databaseId: DatabaseId, registerDriver: RegisterDriver, datas : List[ConnectionData], encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) {
  def buildDBWorker() : ActorRef = try
    registerDrivers()
    val name = "copyworker" + DatabaseIdUtil.databaseIdText(databaseId)
    connectionContext.actorOf(buildSubWorkerProps().withDispatcher("my-pinned-dispatcher"), name)
  catch
    case e: Exception => throw new Exception(s"Building the dbWorker for database $databaseId got", e)

  def buildCopyWorker() : ActorRef = try {
    registerDrivers()
    val name = "copyworker" + DatabaseIdUtil.databaseIdText(databaseId)
    connectionContext.actorOf(Props(new CopyActor(databaseId, datas, encriptionKey, guiActor, localization, keyFilesDirPath)).withDispatcher("my-pinned-dispatcher"), name)
  } catch {
    case e: Exception => throw new Exception(s"Getting the copyworker for $databaseId got", e)
  }

  private def registerDrivers(): Unit =
    datas.foreach(data =>
      try {
        registerDriver.registerDriverIfNeeded(DriverSpec(data.jar, data.driver))
      } catch {
        case c: ClassNotFoundException => throw new Exception(s"Registering the driver ${data.driver} got ClassNotFoundException:", c)
        case t: Throwable => throw new Exception(s"Registering  the driver ${data.driver} got the exception of type ${t.getClass.getCanonicalName}:", t)
      }
    )

  private def buildSubWorkerProps() : Props = {
    Props(classOf[DatabaseActor], databaseId, encriptionKey, datas, guiActor, connectionContext.self, localization, keyFilesDirPath).withDispatcher("my-pinned-dispatcher")
  }

  /*
  private def buildSubWorkerName(index : Int) : String = {
    s"dbworker ${DatabaseIdUtil.databaseIdText(databaseId) + index}"
  }

   */
}

object ConnectionBuilder {
  def buildDBActor(databaseId: DatabaseId, registerDriver: RegisterDriver, datas : List[ConnectionData], encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = {
    val builder = new ConnectionBuilder(databaseId, registerDriver, datas, encriptionKey, guiActor, connectionContext, localization, keyFilesDirPath)
    builder.buildDBWorker()
  }

  def buildCopyWorker(databaseId: DatabaseId, registerDriver: RegisterDriver, datas : List[ConnectionData], encriptionKey : EncryptionKey, guiActor : ActorRef, connectionContext : ActorContext, localization : Localization, keyFilesDirPath: Path) : ActorRef = {
    val builder = new ConnectionBuilder(databaseId, registerDriver, datas, encriptionKey, guiActor, connectionContext, localization, keyFilesDirPath)
    builder.buildCopyWorker()
  }
}
