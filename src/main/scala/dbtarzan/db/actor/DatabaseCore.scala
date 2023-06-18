package dbtarzan.db.actor

import dbtarzan.db.basicmetadata._
import dbtarzan.db.foreignkeys.ForeignKeyLoader
import dbtarzan.db.{DatabaseId, QueryAttributes, QueryLoader, SimpleDatabaseId}
import dbtarzan.localization.Localization
import dbtarzan.messages.TLogger

import java.sql.DatabaseMetaData

/* to be able to reset the connection we need to close the original and create a new one. This puts together the connection
	and everything is dependent by it, so we need only one "var" variable */
case class DBLimits(maxRows : Option[Int], queryTimeoutInSeconds : Option[Int])

class DatabaseCore(connection : java.sql.Connection, databaseId: DatabaseId, val simpleDatabaseId: SimpleDatabaseId, val attributes: QueryAttributes, val limits: DBLimits, localization: Localization, log: TLogger) {
	val foreignKeyLoader = new ForeignKeyLoader(connection, databaseId, simpleDatabaseId, attributes.definition, localization, log)
	val queryLoader = new QueryLoader(connection, log)
  private val metaData: DatabaseMetaData = connection.getMetaData
  val tablesLoader = new MetadataTablesLoader(attributes.definition, metaData)
	val columnsLoader = new MetadataColumnsLoader(attributes.definition, metaData, log)
	val primaryKeysLoader = new MetadataPrimaryKeysLoader(attributes.definition, metaData, log)
	val schemasLoader = new MetadataSchemasLoader(metaData, log)
  val indexesLoader = new MetadataIndexesLoader(attributes.definition, metaData, log)
	def closeConnection(): Unit = connection.close()
}