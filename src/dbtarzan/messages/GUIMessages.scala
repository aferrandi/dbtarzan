package dbtarzan.messages

import dbtarzan.db.{Rows, Fields, ForeignKeys, FollowKey, QueryAttributes, PrimaryKeys, DatabaseId, TableId, TableNames }
import akka.actor.ActorRef

trait TWithDatabaseId { def databaseId : DatabaseId }

trait TWithQueryId { def queryId : QueryId }

trait TWithTableId { def tableId : TableId }
      
case class ResponseRows(queryId : QueryId, rows: Rows) 
    extends TWithQueryId

case class ResponseTables(databaseId : DatabaseId, names: TableNames) 
    extends TWithDatabaseId

case class ResponseCloseTables(databaseId : DatabaseId, ids : List[QueryId]) 
    extends TWithDatabaseId

case class ResponseColumns(tableId  : TableId, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithTableId

case class ResponsePrimaryKeys(queryId : QueryId, keys : PrimaryKeys) 
    extends TWithQueryId

case class ResponseForeignKeys(queryId : QueryId, keys : ForeignKeys) 
    extends TWithQueryId

case class ResponseColumnsFollow(tableId: TableId,  follow : FollowKey, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithTableId

case class ResponseDatabase(databaseId : DatabaseId, dbActor : ActorRef) 
    extends TWithDatabaseId

case class ResponseCloseDatabase(databaseId : DatabaseId) 
    extends TWithDatabaseId

case class RequestRemovalTabsAfter(queryId : QueryId) 
    extends TWithQueryId

case class RequestRemovalTabsBefore(queryId : QueryId) 
    extends TWithQueryId

case class RequestRemovalAllTabs(databaseId : DatabaseId) 
    extends TWithDatabaseId

case class CopySelectionToClipboard(queryId : QueryId, includeHeaders : Boolean) 
    extends TWithQueryId

case class CopySQLToClipboard(queryId : QueryId) 
    extends TWithQueryId

case class CheckAllTableRows(queryId : QueryId) 
    extends TWithQueryId

case class CheckNoTableRows(queryId : QueryId) 
    extends TWithQueryId

case class SwitchRowDetails(queryId : QueryId) 
    extends TWithQueryId

case class ErrorDatabaseAlreadyOpen(databaseId : DatabaseId)

case class DatabaseIds(names : List[DatabaseId])
