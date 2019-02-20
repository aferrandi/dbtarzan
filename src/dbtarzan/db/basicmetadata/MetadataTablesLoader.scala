package dbtarzan.db.basicmetadata

import java.sql.{ DatabaseMetaData, SQLException, ResultSet }

import dbtarzan.db.util.{ ExceptionToText, ResultSetReader }
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.{ TableNames, DBDefinition }

/* to read the basic methadata (tables and columns) from the dataase */
class MetadataTablesLoader(definition: DBDefinition, meta : DatabaseMetaData) {
	private case class TableAndSchema(tableName : String, schema: Option[String])

	/* gets the tables whose names or whose column names match a pattern */
	def tablesByPattern(pattern : String) : TableNames = try {
			val uppercasePattern = pattern.toUpperCase		
			val allStandardTables = using(meta.getTables(definition.catalog.orNull, definition.schema.orNull, "%", Array("TABLE"))) { rs =>
				readTableAndSchemas(rs)
			}
			val tablesByColumnPattern = using(meta.getColumns(definition.catalog.orNull, definition.schema.orNull, "%", "%"+uppercasePattern+"%")) { rs =>
				readTableAndSchemas(rs)
			}.map(toUpperCase).toSet
			val matchingTables = allStandardTables.filter(t => {
				val ut = toUpperCase(t)
				ut.tableName.contains(uppercasePattern) || tablesByColumnPattern.contains(ut)
				})
			toTableNames(matchingTables.map(_.tableName))
		} catch {
			case se : SQLException  => throw new Exception("Reading the tables with pattern "+pattern+" got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the tables with pattern "+pattern+" got", ex)
		}

	/* gets all the tables in the database/schema from the database metadata */
	def tableNames() : TableNames = try {
			using(meta.getTables(definition.catalog.orNull, definition.schema.orNull, "%", Array("TABLE"))) { rs =>
				toTableNames(readTableNames(rs))
			}
		} catch {
			case se : SQLException  => throw new Exception("Reading the database tables got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the database tables got", ex)
		}

	private def toTableNames(tableNames : List[String]) = TableNames(tableNames.sorted)

	private def toUpperCase(tableAndSchema : TableAndSchema) = TableAndSchema(
		tableAndSchema.tableName.toUpperCase, 
		tableAndSchema.schema.map(_.toUpperCase)
		)

	private def readTableNames(rs : ResultSet) : List[String] = 
		ResultSetReader.readRS(rs, _.getString("TABLE_NAME"))

	private def readTableAndSchemas(rs : ResultSet) : List[TableAndSchema] = 
		ResultSetReader.readRS(rs, 
			v => TableAndSchema(v.getString("TABLE_NAME"), Option(v.getString("TABLE_SCHEM")))
		)
}
