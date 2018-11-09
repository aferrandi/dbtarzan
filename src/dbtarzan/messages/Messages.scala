package dbtarzan.messages

import dbtarzan.db.{QuerySql, Rows, TableNames, Fields, ForeignKeys, FollowKey, QueryAttributes, PrimaryKeys, DatabaseId}
import dbtarzan.config.ConnectionData
import akka.actor.ActorRef
import java.time.LocalDateTime

case class QueryRows(tableId : TableId, sql : QuerySql)

case class QueryTables(databaseId : DatabaseId)

case class QueryColumns(databaseId : DatabaseId, tableName : String)

case class QueryColumnsFollow(databaseId : DatabaseId, tableName : String, follow : FollowKey)

case class QueryPrimaryKeys(tableId : TableId)

case class QueryForeignKeys(tableId : TableId)

case class QueryClose(databaseId : DatabaseId)

case class QueryReset(databaseId : DatabaseId)
      
case class ResponseRows(tableId : TableId, rows: Rows)

case class ResponseTables(databaseId : DatabaseId, names: TableNames)

case class ResponseCloseTables(databaseId : DatabaseId, ids : List[TableId])

case class ResponseColumns(databaseId: DatabaseId, tableName : String, columns : Fields, queryAttributes : QueryAttributes)

case class ResponsePrimaryKeys(tableId : TableId, keys : PrimaryKeys)

case class ResponseForeignKeys(tableId : TableId, keys : ForeignKeys)

case class ResponseColumnsFollow(databaseId: DatabaseId, tableName : String,  follow : FollowKey, columns : Fields, queryAttributes : QueryAttributes)

sealed trait TLogMessage{ def produced : LocalDateTime; def text: String }

case class Error(produced : LocalDateTime, text: String, ex : Exception) extends TLogMessage

case class Warning(produced : LocalDateTime, text : String) extends TLogMessage

case class Info(produced : LocalDateTime, text : String) extends TLogMessage

case class ErrorDatabaseAlreadyOpen(databaseId : DatabaseId)

case class QueryDatabase(databaseId : DatabaseId)

case class ResponseDatabase(databaseId : DatabaseId, dbActor : ActorRef)

case class ResponseCloseDatabase(databaseId : DatabaseId)

case class RequestRemovalTabsAfter(tableId : TableId)

case class RequestRemovalTabsBefore(tableId : TableId)

case class RequestRemovalAllTabs(databaseId : DatabaseId)

case class CopyToFile(databaseId : DatabaseId)

case class ConnectionDatas(datas : List[ConnectionData])

case class DatabaseIds(names : List[DatabaseId])

case class CopySelectionToClipboard(tableId : TableId, includeHeaders : Boolean)

case class CopySQLToClipboard(tableId : TableId)

case class CheckAllTableRows(tableId : TableId)

case class CheckNoTableRows(tableId : TableId)

case class SwitchRowDetails(tableId : TableId)


