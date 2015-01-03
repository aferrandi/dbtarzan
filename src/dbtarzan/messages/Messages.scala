package dbtarzan.messages

import dbtarzan.db.{Rows, TableNames, Fields, ForeignKeys, FollowKey}

case class QueryRows(id : TableId, sql : String, maxRows : Int)

case class QueryTables(id : DatabaseId)

case class QueryColumns(id : DatabaseId, tableName : String)

case class QueryColumnsFollow(id : DatabaseId, tableName : String, follow : FollowKey)

case class QueryForeignKeys(id : TableId)

case class ResponseRows(id : TableId, rows: Rows)

case class ResponseTables(id : DatabaseId, names: TableNames)

case class ResponseColumns(id: DatabaseId, tableName : String, columns : Fields)

case class ResponseForeignKeys(id : TableId, keys : ForeignKeys)

case class ResponseColumnsFollow(id: DatabaseId, tableName : String,  follow : FollowKey, columns : Fields)