package dbtarzan.db.actor

import akka.actor.Actor
import akka.actor.ActorRef

import scala.collection.mutable.ListBuffer

import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.password.EncryptionKey
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db._
import dbtarzan.db.foreignkeys.{ ForeignKeyLoader, ForeignKeysFiles }
import dbtarzan.messages._
import dbtarzan.localization.Localization


/* The actor that copies the foreign keys it read from a database to a text file.
The file is then used by DatabaseWorker instead of reading the foreign keys fron the database, 
thus avoiding delays when reading foreign keys from the database is slow (Oracle) */
class CopyWorker(data : ConnectionData, encryptionKey: EncryptionKey, guiActor : ActorRef, localization: Localization) extends Actor {
	val log = new Logger(guiActor)
	val driverManger = new DriverManagerWithEncryption(encryptionKey)
	val connection = driverManger.getConnection(data)
	def databaseName = data.name
	val foreignKeyLoader =  new ForeignKeyLoader(connection, DBDefinition(data.schema, data.catalog), localization)
	val queryLoader = new QueryLoader(connection)
	val foreignKeysFile = ForeignKeysFiles.forCache(databaseName)

	/* gets all the tables in the database/schema from the database metadata */
	private def tableNames() : List[String] = {
		val meta = connection.getMetaData()
		using(meta.getTables(data.catalog.orNull, data.schema.orNull, "%", Array("TABLE"))) { rs =>
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