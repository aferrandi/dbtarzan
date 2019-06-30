package dbtarzan.messages

import dbtarzan.db.{ FollowKey, DatabaseId, TableId, DBTableStructure, ForeignKeysForTableList, AdditionalForeignKey}


case class OriginalQuery(queryId : QueryId, close : Boolean)

case class QueryRows(queryId : QueryId, original : Option[OriginalQuery], structure : DBTableStructure) 

case class QueryTables(databaseId : DatabaseId) 

case class QueryTablesByPattern(databaseId : DatabaseId, pattern: String) 

case class QueryColumns(tableId: TableId) 

case class QueryColumnsForForeignKeys(databaseId : DatabaseId, tableName: String) 

case class QueryColumnsFollow(tableId: TableId, follow : FollowKey) 

case class QueryPrimaryKeys(queryId : QueryId) 

case class QueryForeignKeys(queryId : QueryId) 

case class QueryReset(databaseId : DatabaseId) 

case class UpdateAdditionalForeignKeys(databaseId : DatabaseId, keys : List[AdditionalForeignKey])

case class RequestAdditionalForeignKeys(databaseId : DatabaseId)
