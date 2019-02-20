package dbtarzan.db.basicmetadata

import java.sql.{ DatabaseMetaData, SQLException, ResultSet }

import dbtarzan.db.util.{ ExceptionToText, ResultSetReader }
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.{ PrimaryKeys, PrimaryKey, DBDefinition }

/* to read the basic methadata (tables and columns) from the dataase */
class MetadataPrimaryKeysLoader(definition: DBDefinition, meta : DatabaseMetaData) {
    private case class PrimaryKeyField(keyName : String, fieldName : String)

	def primaryKeys(tableName : String) : PrimaryKeys = try {
			using(meta.getPrimaryKeys(definition.catalog.orNull, definition.schema.orNull, tableName)) { rs =>
				val list = readPrimaryKeys(rs)
				println("Primary keys ("+list.size+") loaded")
				val keys =list.groupBy(_.keyName).map({ 
					case (keyName, fields) => PrimaryKey(keyName, fields.toList.map(_.fieldName))
				}).toList
				PrimaryKeys(keys)
			} 
		} catch {
			case se : SQLException  => throw new Exception("Reading the primary keys of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the primary keys of the "+tableName +" table got", ex)
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
