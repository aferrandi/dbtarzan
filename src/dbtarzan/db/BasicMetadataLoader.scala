package dbtarzan.db

import java.sql.DatabaseMetaData
import scala.collection.mutable.ListBuffer
import dbtarzan.db.util.ResourceManagement.using

class BasicMetadataLoader(schema: Option[String], meta : DatabaseMetaData) {

	/* gets the columns of a table from the database metadata */
	def columnNames(tableName : String) : Fields = {
		using(meta.getColumns(null, schema.orNull, tableName, "%")) { rs =>
			val list = new ListBuffer[Field]()			
			while(rs.next) {
				var fieldName = rs.getString("COLUMN_NAME")
				toType(rs.getInt("DATA_TYPE")).map(fieldType => list += Field(fieldName, fieldType))
			}
			println("Columns loaded")
			Fields(list.toList)
		}
	}

	/* gets all the tables in the database/schema from the database metadata */
	def tableNames() : TableNames = {
		using(meta.getTables(null, schema.orNull, "%", Array("TABLE"))) { rs =>
			val list = new ListBuffer[String]()
			while(rs.next) {
				list += rs.getString("TABLE_NAME")			
			}
			TableNames(list.toList)
		}
	}

	/* converts the database column type to a DBTarzan internal type */
	private def toType(sqlType : Int) : Option[FieldType] = 
		sqlType match {
			case java.sql.Types.CHAR => Some(FieldType.STRING)
			case java.sql.Types.INTEGER => Some(FieldType.INT)
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => Some(FieldType.FLOAT)	
			case _ => Some(FieldType.STRING)
		}
}
