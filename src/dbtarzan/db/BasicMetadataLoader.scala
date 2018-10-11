package dbtarzan.db

import java.sql.{ DatabaseMetaData, SQLException }
import scala.collection.mutable.ListBuffer

import dbtarzan.db.util.ExceptionToText
import dbtarzan.db.util.ResourceManagement.using

/* to read the basic methadata (tables and columns) from the dataase */
class BasicMetadataLoader(schema: Option[String], meta : DatabaseMetaData) {

	/* gets the columns of a table from the database metadata */
	def columnNames(tableName : String) : Fields = try {
			using(meta.getColumns(null, schema.orNull, tableName, "%")) { rs =>
				val list = new ListBuffer[Field]()			
				while(rs.next) {
					val fieldName = rs.getString("COLUMN_NAME")
					toType(rs.getInt("DATA_TYPE")).map(fieldType => list += Field(fieldName, fieldType))
				}
				println("Columns loaded")
				Fields(list.toList)
			}
		}
		catch {
			case se : SQLException  => throw new Exception("Reading the columns of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the columns of the "+tableName +" table got", ex)
		}

	/* gets all the tables in the database/schema from the database metadata */
	def tableNames() : TableNames = try {
			using(meta.getTables(null, schema.orNull, "%", Array("TABLE"))) { rs =>
				val list = new ListBuffer[String]()
				while(rs.next) {
					list += rs.getString("TABLE_NAME")			
				}
				TableNames(list.toList.sorted)
			}
		}			
		catch {
			case se : SQLException  => throw new Exception("Reading the database tables got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the database tables got", ex)
		}

	/* converts the database column type to a DBTarzan internal type */
	private def toType(sqlType : Int) : Option[FieldType] = 
		sqlType match {
			case java.sql.Types.CHAR => Some(FieldType.STRING)
			case java.sql.Types.INTEGER => Some(FieldType.INT)
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => Some(FieldType.FLOAT)	
			case _ => Some(FieldType.STRING)
		}

	def primaryKeys(tableName : String) : PrimaryKeys = try {
		def cleanFieldName(fieldNameRaw: String) : String = fieldNameRaw.trim.stripPrefix("[").stripSuffix("]")
		case class PrimaryKeyField(keyName : String, fieldName : String)
			using(meta.getPrimaryKeys(null, schema.orNull, tableName)) { rs =>
				val list = new ListBuffer[PrimaryKeyField]()			
				while(rs.next) {
					val keyName = rs.getString("PK_NAME")
					val fieldNameRaw = rs.getString("COLUMN_NAME")
					val fieldName = cleanFieldName(fieldNameRaw)
					list += PrimaryKeyField(keyName, fieldName)
				}
				println("Primary keys ("+list.size+") loaded")
				val keys =list.groupBy(_.keyName).map({ 
					case (keyName, fields) => PrimaryKey(keyName, fields.toList.map(_.fieldName))
				}).toList
				PrimaryKeys(keys)
			} 
		}
		catch {
			case se : SQLException  => throw new Exception("Reading the primary keys of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
			case ex : Throwable => throw new Exception("Reading the primary keys of the "+tableName +" table got", ex)
		}

}
