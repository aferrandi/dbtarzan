package dbtarzan.messages

import dbtarzan.db.{ FollowKey, DatabaseId, TableId, QuerySql}

case class QueryRows(queryId : QueryId, sql : QuerySql) 

case class QueryTables(databaseId : DatabaseId) 

case class QueryTablesByPattern(databaseId : DatabaseId, pattern: String) 

case class QueryColumns(tableId: TableId) 

case class QueryColumnsFollow(tableId: TableId, follow : FollowKey) 

case class QueryPrimaryKeys(queryId : QueryId) 

case class QueryForeignKeys(queryId : QueryId) 

case class QueryReset(databaseId : DatabaseId) 

