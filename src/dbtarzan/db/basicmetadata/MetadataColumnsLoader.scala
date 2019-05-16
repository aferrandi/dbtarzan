package dbtarzan.db.basicmetadata

import java.sql.{ DatabaseMetaData, SQLException, ResultSet }

import dbtarzan.db.util.{ ExceptionToText, ResultSetReader }
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.{ Fields, Field, FieldType, DBDefinition }

/* to read the basic methadata (tables and columns) from the dataase */
class MetadataColumnsLoader(definition: DBDefinition, meta : DatabaseMetaData) {
	/* gets the columns of a table from the database metadata */
	def columnNames(tableName : String) : Fields = try {
		using(meta.getColumns(definition.catalog.orNull, definition.schema.orNull, tableName, "%")) { rs =>
			val list = readColumns(rs) 
			println("Columns with schema "+definition+" loaded")
			Fields(list)
		}
	} catch {
		case se : SQLException  => throw new Exception("Reading the columns of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
		case ex : Throwable => throw new Exception("Reading the columns of the "+tableName +" table got", ex)
	}


	/* converts the database column type to a DBTarzan internal type */
	private def toType(sqlType : Int) : FieldType = 
		sqlType match {
			case java.sql.Types.CHAR => FieldType.STRING
			case java.sql.Types.INTEGER => FieldType.INT
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => FieldType.FLOAT	
			case _ => FieldType.STRING
		}

	private def toTypeDescription(typeName : String, columnSize: Option[Int], decimalDigits: Option[Int]) : String =
		typeName+"["+List(columnSize, decimalDigits).flatten.mkString(",")+"]"

	private def readColumns(rs : ResultSet) : List[Field] = 
		ResultSetReader.readRS(rs, r => {
			val fieldName = r.getString("COLUMN_NAME")
			val fieldType = r.getInt("DATA_TYPE")
			val typeName = r.getString("TYPE_NAME")
			val columnSize = Option(r.getInt("COLUMN_SIZE"))
			val decimalDigits = Option(r.getInt("DECIMAL_DIGITS"))
			Field(fieldName, toType(fieldType), toTypeDescription(typeName, columnSize, decimalDigits))
		})
}
