package dbtarzan.db.loader

import dbtarzan.db.QuerySql
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.util.{ExceptionToText, ExecutionTime}
import dbtarzan.messages.TLogger

import java.sql.{ResultSet, SQLException, Statement}
import scala.concurrent.duration.Duration

/** The part of the database actor that runs the table queries */
class QueryRowsNumberLoader(connection : java.sql.Connection, log: TLogger) {
  def query(qry : QuerySql, queryTimeout: Duration) : Int = {
      log.debug( s"Count SQL: ${qry.sql}")
      using(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) { statement =>
        queryWithStatement(statement, qry, queryTimeout)
      }
  }

  private def queryWithStatement(statement: Statement, qry : QuerySql, queryTimeout: Duration) : Int = try
    statement.setQueryTimeout(queryTimeout.toSeconds.toInt)
    using(new ExecutionTime(queryTimeout, "reading the number of rows")) { executionTime =>
      val rs = statement.executeQuery(qry.sql)
      rs.next()
      val count = rs.getInt(1)
      log.debug(s"Row count: ${count}")
      count
    }
  catch
    case se : SQLException  => throw new Exception("With query "+qry.sql+" got "+ExceptionToText.sqlExceptionText(se), se)
    case ex : Throwable => throw new Exception("With query "+qry.sql+" got", ex)
}