package dbtarzan.db.basicmetadata

import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ResultSetReader}
import dbtarzan.db.{DBDefinition, TableNames}

import java.sql.{DatabaseMetaData, ResultSet, SQLException}

case class TableAndSchema(tableName: String, schema: Option[String])

case class ColumnWithTable(columnName: String, tableName: String)


/* to read the basic methadata (tables and columns) from the dataase */
class MetadataTablesLoader(definition: DBDefinition, meta : DatabaseMetaData) {

  /* gets all the tables in the database/schema from the database metadata */
  def tableNames(): TableNames = try {
    using(allTablesFromDB()) { rs =>
      TableNames(readTableNames(rs).sorted)
    }
  } catch {
    case se: SQLException => throw new Exception("Reading the database tables got " + ExceptionToText.sqlExceptionText(se), se)
    case ex: Throwable => throw new Exception("Reading the database tables got", ex)
  }

  def columnNamesWithTables(): List[ColumnWithTable] = try {
    using(allColumnsFromDB()) { rs =>
      readColumnsAndTablesNames(rs)
    }
  } catch {
    case se: SQLException => throw new Exception("Reading the database columns and tables got " + ExceptionToText.sqlExceptionText(se), se)
    case ex: Throwable => throw new Exception("Reading the database columns and tables got", ex)
  }


  private def allColumnsFromDB(): ResultSet = {
    meta.getColumns(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, "%", "%")
  }

  private def allTablesFromDB(): ResultSet = {
    meta.getTables(definition.catalog.orNull, definition.schemaId.map(_.schema.schema).orNull, "%", Array("TABLE"))
  }


  private def readTableNames(rs : ResultSet) : List[String] =
    ResultSetReader.readRS(rs, _.getString("TABLE_NAME"))

  private def readColumnsAndTablesNames(rs : ResultSet) : List[ColumnWithTable] =
    ResultSetReader.readRS(rs,
      v => ColumnWithTable(v.getString("COLUMN_NAME"), v.getString("TABLE_NAME")) // yea, TABLE_SCHEMM, not TABLE_SCHEMMA
    )
}
