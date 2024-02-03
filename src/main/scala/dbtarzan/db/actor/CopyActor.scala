package dbtarzan.db.actor

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorRef

import java.nio.file.Path
import scala.collection.mutable.ListBuffer
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db._
import dbtarzan.db.foreignkeys.ForeignKeyLoader
import dbtarzan.db.foreignkeys.files.ForeignKeysFile
import dbtarzan.messages._
import dbtarzan.localization.Localization

import java.sql.Connection


/* 
  The actor that copies the foreign keys it read from a database to a text file.
  The file is then used by DatabaseWorker instead of reading the foreign keys fron the database,
  thus avoiding delays when reading foreign keys from the database is slow (Oracle)
*/
class CopyActor(databaseId: DatabaseId,
                datas : List[ConnectionData],
                encryptionKey: EncryptionKey,
                guiActor : ActorRef,
                localization: Localization,
                keyFilesDirPath : Path,
                loginPasswords: LoginPasswords,
                log : Logger
               ) extends Actor {
  case class DataWithConnection(data: ConnectionData, connection: Connection)

  private val driverManger = new DriverManagerWithEncryption(encryptionKey)
  private val datasWithConnections: List[DataWithConnection] = datas.map(data => DataWithConnection(data, driverManger.getConnection(data, loginPasswords.loginPasswords.get(SimpleDatabaseId(data.name)))))
  // private val queryLoader = new QueryLoader(connection, log)
  private val foreignKeysFile = new ForeignKeysFile(keyFilesDirPath, DatabaseIdUtil.databaseIdText(databaseId))

  /* gets all the tables in the database/schema from the database metadata */
  private def tableNames(dataWithConnection: DataWithConnection) : List[String] = {
    val meta = dataWithConnection.connection.getMetaData
    using(meta.getTables(dataWithConnection.data.catalog.orNull, dataWithConnection.data.schema.map(_.schema).orNull, "%", Array("TABLE"))) { rs =>
      val list = new ListBuffer[String]()
      while(rs.next) {
        list += rs.getString("TABLE_NAME")
      }
      list.toList
    }
  }

  /* loads the keys from the database (table by table) and saves them to the file */
  def loadAllKeysAndWriteThemToFile() : Unit  = {
    datasWithConnections.foreach(dataWithConnection => {
      val names = tableNames(dataWithConnection)
      val data = dataWithConnection.data
      val simpleDatabaseId = SimpleDatabaseId(data.name)
      val schemaId = data.schema.map(schema => SchemaId(databaseId, simpleDatabaseId, schema))
      val keysForTables = names.map(name => {
        val tableId = TableId(databaseId, simpleDatabaseId, name)
        val foreignKeyLoader = new ForeignKeyLoader(dataWithConnection.connection, databaseId, simpleDatabaseId, DBDefinition(schemaId, data.catalog), log)
        ForeignKeysForTable(tableId, foreignKeyLoader.foreignKeys(tableId))
      })
      foreignKeysFile.writeAsFile(keysForTables)
    })
  }

  private def copyToFIle(): Unit = {
    val filePath = keyFilesDirPath.resolve(DatabaseIdUtil.databaseIdText(databaseId))
    log.info(localization.writingFile(filePath))
    loadAllKeysAndWriteThemToFile()
    log.info(localization.fileWritten(filePath))
    context.stop(self)
  }

  def receive: Receive = {
    case CopyToFile => copyToFIle()
  }
}