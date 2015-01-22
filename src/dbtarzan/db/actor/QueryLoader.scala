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
	
	def query(qry : QueryRows, use : Rows => Unit) : Unit = {
		println("SQL:"+qry.sql)
  		using(connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) { statement => 
	  		val rs = statement.executeQuery(qry.sql)
	  		val meta = rs.getMetaData()
	  		val columnCount = meta.getColumnCount()
	  		println("Column count:"+columnCount+". Rows to read :"+qry.maxRows)
	  		var rows = Vector.empty[Row]
	  		var i = 0
	  		while(rs.next() && i < qry.maxRows) {
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
	private def nextRow(rs : ResultSet, columnCount : Int) : Row = 
		Row(Range(1, columnCount+1).map(i => rs.getString(i)).toList)

}