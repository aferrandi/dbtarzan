package dbtarzan.messages

case class DatabaseId(databaseName : String)

case class TableId(databaseId : DatabaseId, tableName : String, uuid : String)

object IDGenerator {
	def databaseId(databaseName : String) = DatabaseId(databaseName)

	def tableId(databaseId : DatabaseId, tableName : String) = TableId(databaseId, tableName, java.util.UUID.randomUUID.toString) 
}