package dbtarzan.db.basicmetadata

import java.sql.{DatabaseMetaData, ResultSet, SQLException}
import dbtarzan.db.util.{ExceptionToText, ResultSetReader}
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.{DBDefinition, PrimaryKey, PrimaryKeys}
import dbtarzan.messages.Logger

/* to read the basic metadata (tables and columns) from the database */
class MetadataPrimaryKeysLoader(definition: DBDefinition, meta : DatabaseMetaData, log: Logger) {
    private case class PrimaryKeyField(keyName : String, fieldName : String)

	def primaryKeys(tableName : String) : PrimaryKeys = try {
			using(meta.getPrimaryKeys(definition.catalog.orNull, definition.schema.map(_.name).orNull, tableName)) { rs =>
				val rawKeysFields = readPrimaryKeys(rs)
				log.debug("Primary keys ("+rawKeysFields.size+") loaded")
        buildPrimaryKeysFromFields(rawKeysFields)
      }
		} catch {
			case se : SQLException  => throw new Exception("Reading the primary keys of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the primary keys of the "+tableName +" table got", ex)
		}

  private def buildPrimaryKeysFromFields(rawKeysFields: List[PrimaryKeyField]): PrimaryKeys = {
    val keys = rawKeysFields.groupBy(_.keyName).map({
      case (keyName, fields) => PrimaryKey(keyName, fields.toList.map(_.fieldName))
    }).toList
    PrimaryKeys(keys)
  }

  private def cleanFieldName(fieldNameRaw: String) : String =
      fieldNameRaw.trim.stripPrefix("[").stripSuffix("]")

	private def readPrimaryKeys(rs : ResultSet) : List[PrimaryKeyField] = 
		ResultSetReader.readRS(rs, r => {
			val keyName = r.getString("PK_NAME")
			val fieldNameRaw = r.getString("COLUMN_NAME")
			val fieldName = cleanFieldName(fieldNameRaw)
			PrimaryKeyField(keyName, fieldName)
		})
}
