package dbtarzan.messages

import dbtarzan.db.DatabaseId

case class TableId(databaseId : DatabaseId, tableName : String, uuid : String)

object IDGenerator {
	def tableId(databaseId : DatabaseId, tableName : String) = TableId(databaseId, tableName, java.util.UUID.randomUUID.toString) 
}