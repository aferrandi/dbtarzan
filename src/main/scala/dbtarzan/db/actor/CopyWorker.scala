package dbtarzan.db.actor

import akka.actor.Actor
import akka.actor.ActorRef
import java.nio.file.Path
import scala.collection.mutable.ListBuffer

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ ForeignKeyLoader, ForeignKeysFile }
import dbtarzan.messages._
import dbtarzan.localization.Localization


/* 
	The actor that copies the foreign keys it read from a database to a text file.
	The file is then used by DatabaseWorker instead of reading the foreign keys fron the database, 
	thus avoiding delays when reading foreign keys from the database is slow (Oracle) 
*/
class CopyWorker(data : ConnectionData, encryptionKey: EncryptionKey, guiActor : ActorRef, localization: Localization, keyFilesDirPath : Path) extends Actor {
	private val log = new Logger(guiActor)
  private val driverManger = new DriverManagerWithEncryption(encryptionKey)
  private val connection = driverManger.getConnection(data)
  private def databaseName = data.name
  private val foreignKeyLoader =  new ForeignKeyLoader(connection, DBDefinition(data.schema, data.catalog), localization)
  private val queryLoader = new QueryLoader(connection)
  private val foreignKeysFile = new ForeignKeysFile(keyFilesDirPath, databaseName)
	
	/* gets all the tables in the database/schema from the database metadata */
	private def tableNames() : List[String] = {
		val meta = connection.getMetaData
		using(meta.getTables(data.catalog.orNull, data.schema.map(_.name).orNull, "%", Array("TABLE"))) { rs =>
			val list = new ListBuffer[String]()
			while(rs.next) {
				list += rs.getString("TABLE_NAME")			
			}
			list.toList
		}
	}

	/* loads the keys from the database (table by table) and saves them to the file */
	def loadAllKeysAndWriteThemToFile() : Unit  = {
		val names = tableNames()
		val keysForTables = names.map(name => 
			ForeignKeysForTable(name, foreignKeyLoader.foreignKeys(name))
			)
		foreignKeysFile.toFile(ForeignKeysForTableList(keysForTables))
	}

  	def receive = {
	    case CopyToFile => {
	    	log.info(localization.writingFile(foreignKeysFile.fileName))
	    	loadAllKeysAndWriteThemToFile()
	    	log.info(localization.fileWritten(foreignKeysFile.fileName))
	    }
  	}
}