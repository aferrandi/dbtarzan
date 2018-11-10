package dbtarzan.messages

import dbtarzan.db.TableId

case class QueryId(tableId : TableId, uuid : String)

object IDGenerator {
	def tableId(tableId : TableId) = QueryId(tableId, java.util.UUID.randomUUID.toString) 
}