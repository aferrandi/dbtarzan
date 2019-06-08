package dbtarzan.messages

import dbtarzan.db.{ FollowKey, DatabaseId, TableId, DBTableStructure, ForeignKeysForTableList}


case class OriginalQuery(queryId : QueryId, close : Boolean)

case class QueryRows(queryId : QueryId, original : Option[OriginalQuery], structure : DBTableStructure) 

case class QueryTables(databaseId : DatabaseId) 

case class QueryTablesByPattern(databaseId : DatabaseId, pattern: String) 

case class QueryColumns(tableId: TableId) 

case class QueryColumnsFollow(tableId: TableId, follow : FollowKey) 

case class QueryPrimaryKeys(queryId : QueryId) 

case class QueryForeignKeys(queryId : QueryId) 

case class QueryReset(databaseId : DatabaseId) 

case class UpdateAdditionalForeignKeys(databaseId : DatabaseId, keys : ForeignKeysForTableList)

case class RequestAdditionalForeignKeys(databaseId : DatabaseId)
