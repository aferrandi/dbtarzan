package dbtarzan.db.actor

import dbtarzan.db.basicmetadata.{MetadataColumnsLoader, MetadataIndexesLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader, MetadataTablesLoader}
import dbtarzan.db.foreignkeys.ForeignKeyLoader
import dbtarzan.db.{DBDefinition, QueryLoader}
import dbtarzan.localization.Localization
import dbtarzan.messages.TLogger

import java.sql.DatabaseMetaData

/* to be able to reset the connection we need to close the original and create a new one. This puts together the connection
	and everything is dependent by it, so we need only one "var" variable */
class DatabaseWorkerCore(connection : java.sql.Connection, definition: DBDefinition, maxFieldSize: Option[Int], localization: Localization, log: TLogger) {
	val foreignKeyLoader = new ForeignKeyLoader(connection, definition, localization, log)
	val queryLoader = new QueryLoader(connection, log)
  private val metaData: DatabaseMetaData = connection.getMetaData
  val tablesLoader = new MetadataTablesLoader(definition, metaData)
	val columnsLoader = new MetadataColumnsLoader(definition, metaData, log)
	val primaryKeysLoader = new MetadataPrimaryKeysLoader(definition, metaData, log)
	val schemasLoader = new MetadataSchemasLoader(metaData, log)
  val indexesLoader = new MetadataIndexesLoader(definition, metaData, log)
	def closeConnection(): Unit = connection.close()
}