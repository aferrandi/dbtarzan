package dbtarzan.db.actor

import java.sql.{Connection, ResultSet}
import scala.collection.immutable.Vector
import dbtarzan.db.util.ResourceManagement.using
import dbtarzan.db.{ Row, Rows}
import dbtarzan.messages.QueryRows


/**
	The part of the database actor that runs the table queries
*/
class QueryLoader(connection : java.sql.Connection) {
	/* does the queries in the database. Sends them back to the GUI in packets of 20 lines 
	   QueryRows gives the SQL query and tells how many rows must be read in total */
	def query(qry : QueryRows, maxRows: Int, use : Rows => Unit) : Unit = {
		println("SQL:"+qry.sql)
  		using(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) { statement => 
	  		val rs = statement.executeQuery(qry.sql)
	  		val meta = rs.getMetaData()
	  		val columnCount = meta.getColumnCount()
	  		println("Column count:"+columnCount+". Rows to read :"+maxRows)
	  		var rows = Vector.empty[Row]
	  		var i = 0
	  		while(rs.next() && i < maxRows) {
	  			rows = rows :+ nextRow(rs, columnCount)
	  			if(rows.length >= 20) {
	  				use(Rows(rows.toList))
	  				rows = Vector.empty[Row]
	  			}
	  			i += 1
	  		}
	  		use(Rows(rows.toList)) // send at least something so that the GUI knows that the task is terminated
	  		println("Query terminated")
		}
	}
	/* converts the current row in the result set to a Row object, that can be sent to the GUI actor */
	private def nextRow(rs : ResultSet, columnCount : Int) : Row = 
		Row(Range(1, columnCount+1).map(i => rs.getString(i)).toList)

}