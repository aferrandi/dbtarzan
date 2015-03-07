package dbtarzan.messages

import dbtarzan.db.{Rows, TableNames, Fields, ForeignKeys, FollowKey}
import akka.actor.ActorRef

case class QueryRows(id : TableId, sql : String, maxRows : Int)

case class QueryTables(id : DatabaseId)

case class QueryColumns(id : DatabaseId, tableName : String)

case class QueryColumnsFollow(id : DatabaseId, tableName : String, follow : FollowKey)

case class QueryForeignKeys(id : TableId)

case class QueryClose(databaseName : String)

case class ResponseRows(id : TableId, rows: Rows)

case class ResponseTables(id : DatabaseId, names: TableNames)

case class ResponseColumns(id: DatabaseId, tableName : String, columns : Fields)

case class ResponseForeignKeys(id : TableId, keys : ForeignKeys)

case class ResponseColumnsFollow(id: DatabaseId, tableName : String,  follow : FollowKey, columns : Fields)

sealed trait TTextMessage

case class Error(ex : Exception) extends TTextMessage

case class Warning(text : String) extends TTextMessage

case class ErrorDatabaseAlreadyOpen(databaseName : String)

case class QueryDatabase(databaseName : String)

case class ResponseDatabase(databaseName : String, dbActor : ActorRef)

case class ResponseClose(databaseName : String)