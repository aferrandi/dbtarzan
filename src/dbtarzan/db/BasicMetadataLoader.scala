package dbtarzan.db

import java.sql.{ DatabaseMetaData, SQLException, ResultSet }
import scala.collection.mutable.ListBuffer

import dbtarzan.db.util.ExceptionToText
import dbtarzan.db.util.ResourceManagement.using

/* to read the basic methadata (tables and columns) from the dataase */
class BasicMetadataLoader(schema: Option[String], meta : DatabaseMetaData) {

	/* gets the columns of a table from the database metadata */
	def columnNames(tableName : String) : Fields = try {
		using(meta.getColumns(null, schema.orNull, tableName, "%")) { rs =>
			val list = readRS(rs, r => {
				val fieldName = r.getString("COLUMN_NAME")
				val fieldType = r.getInt("DATA_TYPE")
				Field(fieldName, toType(fieldType))
			})
			println("Columns loaded")
			Fields(list)
		}
	}
	catch {
		case se : SQLException  => throw new Exception("Reading the columns of the "+tableName +" table got "+ExceptionToText.sqlExceptionText(se), se)
		case ex : Throwable => throw new Exception("Reading the columns of the "+tableName +" table got", ex)
	}

	private def toTableNames(tableNames : List[String]) = TableNames(tableNames.sorted)

	/* gets the tables whose names or whose column names match a pattern */
	def tablesByPattern(pattern : String) : TableNames = try {
		val uppercasePattern = pattern.toUpperCase		
		val allStandardTableNames = using(meta.getTables(null, schema.orNull, "%", Array("TABLE"))) { rs =>
			readTableNames(rs)
		}
		val tablesByColumnPattern = using(meta.getColumns(null, schema.orNull, "%", "%"+uppercasePattern+"%")) { rs =>
			readTableNames(rs)
		}.map(_.toUpperCase).toSet
		val matchingTableNames = allStandardTableNames.filter(t => {
			val ut = t.toUpperCase
			ut.contains(uppercasePattern) || tablesByColumnPattern.contains(ut)
			})
		toTableNames(matchingTableNames)
	}
	catch {
		case se : SQLException  => throw new Exception("Reading the tables with pattern "+pattern+" got "+ExceptionToText.sqlExceptionText(se), se)
		case ex : Throwable => throw new Exception("Reading the tables with pattern "+pattern+" got", ex)
	}


	private def readRS[T](rs : ResultSet, extract : ResultSet => T) : List[T] = {
		val list = new ListBuffer[T]()
		while(rs.next) {
			list += extract(rs)			
		}
		list.toList		
	}

	private def readTableNames(rs : ResultSet) : List[String] = 
		readRS(rs, _.getString("TABLE_NAME"))


	/* gets all the tables in the database/schema from the database metadata */
	def tableNames() : TableNames = try {
		using(meta.getTables(null, schema.orNull, "%", Array("TABLE"))) { rs =>
			toTableNames(readTableNames(rs))
		}
	}			
	catch {
		case se : SQLException  => throw new Exception("Reading the database tables got "+ExceptionToText.sqlExceptionText(se), se)
		case ex : Throwable => throw new Exception("Reading the database tables got", ex)
	}

	/* converts the database column type to a DBTarzan internal type */
	private def toType(sqlType : Int) : FieldType = 
		sqlType match {
			case java.sql.Types.CHAR => FieldType.STRING
			case java.sql.Types.INTEGER => FieldType.INT
			case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => FieldType.FLOAT	
			case _ => FieldType.STRING
		}

	def primaryKeys(tableName : String) : PrimaryKeys = try {
		def cleanFieldName(fieldNameRaw: String) : String = fieldNameRaw.trim.stripPrefix("[").stripSuffix("]")
		case class PrimaryKeyField(keyName : String, fieldName : String)
			using(meta.getPrimaryKeys(null, schema.orNull, tableName)) { rs =>
				val list = readRS(rs, r => {
					val keyName = r.getString("PK_NAME")
					val fieldNameRaw = r.getString("COLUMN_NAME")
					val fieldName = cleanFieldName(fieldNameRaw)
					PrimaryKeyField(keyName, fieldName)
				})
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
