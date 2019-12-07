package dbtarzan.db.actor

import dbtarzan.db.{QueryLoader, DBDefinition }
import dbtarzan.db.foreignkeys.ForeignKeyLoader
import dbtarzan.db.basicmetadata.{MetadataTablesLoader, MetadataColumnsLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader} 
import dbtarzan.localization.Localization

/* to be able to reset the connection we need to close the original and create a new one. This puts together the connection
	and everything is dependent by it, so we need only one "var" variable */
class DatabaseWorkerCore(connection : java.sql.Connection, definition: DBDefinition, localization: Localization) {
	val foreignKeyLoader = new ForeignKeyLoader(connection, definition, localization)
	val queryLoader = new QueryLoader(connection)
	val tablesLoader = new MetadataTablesLoader(definition, connection.getMetaData())
	val columnsLoader = new MetadataColumnsLoader(definition, connection.getMetaData())
	val primaryKeysLoader = new MetadataPrimaryKeysLoader(definition, connection.getMetaData())
	val schemasLoader = new MetadataSchemasLoader(connection.getMetaData())
	def closeConnection() = connection.close()
}