package dbtarzan.db.basicmetadata

import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ResultSetReader}
import dbtarzan.db.{DBDefinition, Field, FieldType, Fields}
import dbtarzan.messages.TLogger

import java.sql.{DatabaseMetaData, ResultSet, SQLException}

/* to read the basic methadata (tables and columns) from the dataase */
class MetadataColumnsLoader(definition: DBDefinition, meta : DatabaseMetaData, log: TLogger) {
  /* gets the columns of a table from the database metadata */
  def columnNames(tableName: String): Fields = try {
    using(meta.getColumns(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, tableName, "%")) { rs =>
      val list = readColumns(rs)
      log.debug(s"Columns with schema $definition loaded")
      Fields(list)
    }
  } catch {
    case se: SQLException => throw new Exception(s"Reading the columns of the $tableName table got ${ExceptionToText.sqlExceptionText(se)}", se)
    case ex: Throwable => throw new Exception(s"Reading the columns of the $tableName table got", ex)
  }
  /* converts the database column type to a DBTarzan internal type */
  private def toType(sqlType: Int): FieldType =
    sqlType match {
      case java.sql.Types.CHAR | java.sql.Types.VARCHAR | java.sql.Types.LONGVARCHAR | java.sql.Types.CLOB |
          java.sql.Types.NCHAR | java.sql.Types.NVARCHAR | java.sql.Types.LONGNVARCHAR | java.sql.Types.NCLOB  => FieldType.STRING
      case java.sql.Types.INTEGER => FieldType.INT
      case java.sql.Types.FLOAT | java.sql.Types.DOUBLE => FieldType.FLOAT
      case java.sql.Types.BINARY | java.sql.Types.BLOB | java.sql.Types.VARBINARY | java.sql.Types.LONGVARBINARY => FieldType.BINARY
      case _ => FieldType.OTHER
    }

  private def toSizeDescription(columnSize: Option[Int], decimalDigits: Option[Int]): Option[String] = {
    def isValidSize(number: Int) = number >= 0 && number <= 1000

    val validSizes = List(columnSize, decimalDigits)
      .map(_.filter(isValidSize))
      .takeWhile(_.isDefined)
      .flatten
    if (validSizes.nonEmpty)
      Some(validSizes.mkString("[", ",", "]"))
    else
      None
  }

  private def toNullableDescription(nullable: Int): Option[String] = nullable match {
    case DatabaseMetaData.columnNoNulls => None
    case DatabaseMetaData.columnNullable => Some("NULL")
    case _ => Some("NULL?")
  }

  private def toTypeDescription(typeName: String, columnSize: Option[Int], decimalDigits: Option[Int], nullable: Int): String =
    List(Some(typeName), toSizeDescription(columnSize, decimalDigits), toNullableDescription(nullable)).flatten.mkString(" ")


  private def readColumns(rs: ResultSet): List[Field] = {
    case class RawField(fieldName: String, fieldType: Int, typeName: String, columnSize: Option[Int], decimalDigits: Option[Int], nullable: Int)
    def fromRawFieldToField(rawField: RawField): Field = {
      val fieldType = toType(rawField.fieldType)
      val description = toTypeDescription(rawField.typeName, rawField.columnSize, rawField.decimalDigits, rawField.nullable)
      val fieldSize = if (fieldType == FieldType.STRING) rawField.columnSize else None
      Field(rawField.fieldName, fieldType, description, fieldSize)
    }
    val rawFields = ResultSetReader.readRS(rs, r => {
      RawField(
        fieldName = r.getString("COLUMN_NAME"),
        fieldType = r.getInt("DATA_TYPE"),
        typeName = r.getString("TYPE_NAME"),
        columnSize =  Some(r.getInt("COLUMN_SIZE")),
        decimalDigits = Some(r.getInt("DECIMAL_DIGITS")),
        nullable = r.getInt("NULLABLE")
      )
    })
//    log.info(s"RawField: $rawFields")
    val fields = rawFields.map(fromRawFieldToField)
    // println(s"Field: $fields")
    fields
  }


}
