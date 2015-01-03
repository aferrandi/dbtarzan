package dbtarzan.messages

case class DatabaseId(databaseName : String)

case class TableId(databaseName : String, tableName : String, uuid : String)

object IDGenerator {
	def databaseId(database : String) = DatabaseId(database)

	def tableId(database : String, tableName : String) = TableId(database, tableName, java.util.UUID.randomUUID.toString) 
}