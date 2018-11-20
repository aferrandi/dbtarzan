package dbtarzan.db.util

import java.sql.ResultSet
import scala.collection.mutable.ListBuffer

object ResultSetReader {
	def readRS[T](rs : ResultSet, extract : ResultSet => T) : List[T] = {
		val list = new ListBuffer[T]()
		while(rs.next) {
			list += extract(rs)			
		}
		list.toList		
	}
}