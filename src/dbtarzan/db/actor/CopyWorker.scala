package dbtarzan.db.actor

import java.sql.{Connection, ResultSet, DriverManager}
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import dbtarzan.config.ConnectionData
import dbtarzan.db.util.ResourceManagement.using
import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.messages._




/* The actor that copies the foreign keys it read from a database to a text file */
class CopyWorker(data : ConnectionData, guiActor : ActorRef) extends Actor {
	val connection = DriverManager.getConnection(data.url, data.user, data.password)
	def databaseName = data.name
	val foreignKeyLoader =  new ForeignKeyLoader(connection, data.schema)
	val queryLoader = new QueryLoader(connection)
	/* gets all the tables in the database/schema from the database metadata */
	private def tableNames() : List[String] = {
		val meta = connection.getMetaData()
		using(meta.getTables(null, data.schema.orNull, "%", Array("TABLE"))) { rs =>
			val list = new ListBuffer[String]()
			while(rs.next) {
				list += rs.getString("TABLE_NAME")			
			}
			list.toList
		}
	}

	def loadAllKeysAndWriteThemToFile() : Unit  = {
		val names = tableNames()
		val keysForTables = names.map(name => 
			ForeignKeysForTable(name, foreignKeyLoader.foreignKeys(name))
			)
		ForeignKeysToFile.toFile(data.name, ForeignKeysForTableList(keysForTables))
	}


  	def receive = {
	    case CopyToFile => loadAllKeysAndWriteThemToFile()
  	}
}