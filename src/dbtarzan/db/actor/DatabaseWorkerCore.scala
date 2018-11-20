package dbtarzan.db.actor

import dbtarzan.db.{QueryLoader, ForeignKeyLoader }
import dbtarzan.db.basicmetadata.{MetadataTablesLoader, MetadataColumnsLoader, MetadataPrimaryKeysLoader} 


/* to be able to reset the connection we need to close the original and create a new one. This puts together the connection
	and everything is dependent by it, so we need only one "var" variable */
class DatabaseWorkerCore(connection : java.sql.Connection, schema: Option[String]) {
	val foreignKeyLoader =  new ForeignKeyLoader(connection, schema)
	val queryLoader = new QueryLoader(connection)
	val tablesLoader = new MetadataTablesLoader(schema, connection.getMetaData())
	val columnsLoader = new MetadataColumnsLoader(schema, connection.getMetaData())
	val primaryKeysLoader = new MetadataPrimaryKeysLoader(schema, connection.getMetaData())
	def closeConnection() = connection.close()
}