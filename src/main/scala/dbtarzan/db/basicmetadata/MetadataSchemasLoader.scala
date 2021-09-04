package dbtarzan.db.basicmetadata

import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ResultSetReader}
import dbtarzan.db.{Schema, Schemas}
import dbtarzan.messages.TLogger

import java.sql.{DatabaseMetaData, ResultSet, SQLException}

/* to read the basic methadata (tables and columns) from the dataase */
class MetadataSchemasLoader(meta : DatabaseMetaData, log: TLogger) {
	/* gets the columns of a table from the database metadata */
	def schemasNames() : Schemas = try {
		using(meta.getSchemas()) { rs =>
			val list = readSchemas(rs) 
			log.debug("Schemas loaded")
			Schemas(list)
		}
	} catch {
		case se : SQLException  => throw new Exception("Reading the schemas got "+ExceptionToText.sqlExceptionText(se), se)
		case ex : Throwable => throw new Exception("Reading the schemas got", ex)
	}

	private def readSchemas(rs : ResultSet) : List[Schema] = 
		ResultSetReader.readRS(rs, r => {
			val schemaName = r.getString("TABLE_SCHEM")
			Schema(schemaName)
		})
}
