package dbtarzan.db.loader

import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ExecutionTime}
import dbtarzan.db.{Field, FieldType, Fields, QuerySql, Row, Rows}
import dbtarzan.messages.TLogger
import dbtarzan.types.Binaries.Binary

import java.sql.{ResultSet, SQLException, Statement}
import scala.concurrent.duration.Duration



/** The part of the database actor that runs the table queries */
class QueryLoader(connection : java.sql.Connection, log: TLogger) {
  private val BUNDLE_SIZE = 20

  /* does the queries in the database. Sends them back to the GUI in packets of 20 lines
       QueryRows gives the SQL query and tells how many rows must be read in total */
  def query(qry : QuerySql, maxRows: Int, queryTimeout: Duration, maxFieldSize: Option[Int], columns: Fields, use : Rows => Unit) : Unit = {
      log.debug( s"SQL: ${qry.sql}")
      using(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) { statement =>
        queryWithStatement(statement, qry, maxRows, queryTimeout, maxFieldSize, columns, use)
      }
  }
  /* converts the current row in the result set to a Row object, that can be sent to the GUI actor */
  private def nextRow(rs : ResultSet, columnCount : Int, columns: Fields) : Row =
    Row(Range(1, columnCount+1).zip(columns.fields).map((i, column) => rowsetToValue(rs, i, column)).toList)
  
  private def queryWithStatement(statement: Statement, qry : QuerySql, maxRows: Int, queryTimeout: Duration, maxFieldSize: Option[Int], columns: Fields, use : Rows => Unit) : Unit = try
    statement.setQueryTimeout(queryTimeout.toSeconds.toInt)
    statement.setMaxRows(maxRows)
    maxFieldSize.foreach(statement.setMaxFieldSize)
    using(new ExecutionTime(queryTimeout, "reading rows")) { executionTime =>
      val rs = statement.executeQuery(qry.sql)
      val meta = rs.getMetaData
      val columnCount = meta.getColumnCount
      log.debug(s"Column count: $columnCount. Rows to read :$maxRows")
      var rows = Vector.empty[Row]
      var i = 0
      while (i < maxRows && !executionTime.isOver && rs.next()) {
        rows = rows :+ nextRow(rs, columnCount, columns)
        if (rows.length >= BUNDLE_SIZE) {
          use(Rows(rows.toList))
          rows = Vector.empty[Row]
        }
        i += 1
      }
      use(Rows(rows.toList)) // send at least something so that the GUI knows that the task is terminated
    }
    log.debug("Query terminated")
  catch
    case se : SQLException  => throw new Exception("With query "+qry.sql+" got "+ExceptionToText.sqlExceptionText(se), se)
    case ex : Throwable => throw new Exception("With query "+qry.sql+" got", ex)

  private def rowsetToValue(rs: ResultSet, i: Int, column: Field): String|Int|Double|Binary = {
    column.fieldType match {
      case FieldType.STRING => rs.getString(i)
      case FieldType.INT => rs.getInt(i)
      case FieldType.FLOAT => rs.getDouble(i)
      case FieldType.BINARY => Binary(rs.getBytes(i))
      case FieldType.OTHER => rs.getString(i)
    }
  }
}