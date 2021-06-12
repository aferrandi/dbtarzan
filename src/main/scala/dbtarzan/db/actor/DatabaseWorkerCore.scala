package dbtarzan.db.actor

import dbtarzan.db.{DBDefinition, QueryLoader}
import dbtarzan.db.foreignkeys.ForeignKeyLoader
import dbtarzan.db.basicmetadata.{MetadataColumnsLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader, MetadataTablesLoader}
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger

/* to be able to reset the connection we need to close the original and create a new one. This puts together the connection
	and everything is dependent by it, so we need only one "var" variable */
class DatabaseWorkerCore(connection : java.sql.Connection, definition: DBDefinition, localization: Localization, log: Logger) {
	val foreignKeyLoader = new ForeignKeyLoader(connection, definition, localization)
	val queryLoader = new QueryLoader(connection, log)
	val tablesLoader = new MetadataTablesLoader(definition, connection.getMetaData)
	val columnsLoader = new MetadataColumnsLoader(definition, connection.getMetaData, log)
	val primaryKeysLoader = new MetadataPrimaryKeysLoader(definition, connection.getMetaData, log)
	val schemasLoader = new MetadataSchemasLoader(connection.getMetaData, log)
	def closeConnection(): Unit = connection.close()
}