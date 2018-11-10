package dbtarzan.messages

import dbtarzan.db.{ FollowKey, DatabaseId, QuerySql}

case class QueryRows(tableId : TableId, sql : QuerySql) 

case class QueryTables(databaseId : DatabaseId) 

case class QueryColumns(databaseId : DatabaseId, tableName : String) 

case class QueryColumnsFollow(databaseId : DatabaseId, tableName : String, follow : FollowKey) 

case class QueryPrimaryKeys(tableId : TableId) 

case class QueryForeignKeys(tableId : TableId) 

case class QueryReset(databaseId : DatabaseId) 

