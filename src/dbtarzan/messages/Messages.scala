package dbtarzan.messages

import dbtarzan.db.{Rows, TableNames, Fields, ForeignKeys, FollowKey, IdentifierDelimiters}
import dbtarzan.config.ConnectionData
import akka.actor.ActorRef

case class QueryRows(id : TableId, sql : String, maxRows : Int)

case class QueryTables(id : DatabaseId)

case class QueryColumns(id : DatabaseId, tableName : String)

case class QueryColumnsFollow(id : DatabaseId, tableName : String, follow : FollowKey)

case class QueryForeignKeys(id : TableId)

case class QueryClose(databaseName : String)

case class ResponseRows(id : TableId, rows: Rows)

case class ResponseTables(id : DatabaseId, names: TableNames)

case class ResponseCloseTables(id : DatabaseId, ids : List[TableId])

case class ResponseColumns(id: DatabaseId, tableName : String, columns : Fields, delimiters : Option[IdentifierDelimiters])

case class ResponseForeignKeys(id : TableId, keys : ForeignKeys)

case class ResponseColumnsFollow(id: DatabaseId, tableName : String,  follow : FollowKey, columns : Fields, delimiters : Option[IdentifierDelimiters])

sealed trait TLogMessage

case class Error(text: String, ex : Exception) extends TLogMessage

case class Warning(text : String) extends TLogMessage

case class Info(text : String) extends TLogMessage

case class ErrorDatabaseAlreadyOpen(databaseName : String)

case class QueryDatabase(databaseName : String)

case class ResponseDatabase(databaseName : String, dbActor : ActorRef)

case class ResponseCloseDatabase(databaseName : String)

case class CopyToFile(databaseName : String)

case class ConnectionDatas(datas : List[ConnectionData])

case class DatabaseNames(names : List[String])
