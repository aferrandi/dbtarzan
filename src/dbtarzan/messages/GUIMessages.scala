package dbtarzan.messages

import dbtarzan.db._
import akka.actor.ActorRef

trait TWithDatabaseId { def databaseId : DatabaseId }

trait TWithQueryId { def queryId : QueryId }

trait TWithTableId { def tableId : TableId }
      
case class ResponseRows(queryId : QueryId, structure : DBTableStructure, rows : Rows, original : Option[OriginalQuery]) 
    extends TWithQueryId

case class ErrorRows(queryId : QueryId,  ex: Exception) 
    extends TWithQueryId

case class ResponseTables(databaseId : DatabaseId, names: TableNames) 
    extends TWithDatabaseId

case class ResponseCloseTables(databaseId : DatabaseId, ids : List[QueryId]) 
    extends TWithDatabaseId

case class ResponseColumns(tableId  : TableId, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithTableId

case class  ResponseColumnsForForeignKeys(databaseId : DatabaseId, tableName: String, columns : Fields) 
    extends TWithDatabaseId

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
    
case class RequestRemovalThisTab(queryId : QueryId) 
    extends TWithQueryId

case class RequestRemovalAllTabs(databaseId : DatabaseId) 
    extends TWithDatabaseId

case class CopySelectionToClipboard(queryId : QueryId, includeHeaders : Boolean) 
    extends TWithQueryId

case class CheckAllTableRows(queryId : QueryId) 
    extends TWithQueryId

case class CheckNoTableRows(queryId : QueryId) 
    extends TWithQueryId

case class SwitchRowDetails(queryId : QueryId) 
    extends TWithQueryId

case class RequestOrderByField(queryId : QueryId, field : Field) 
    extends TWithQueryId

case class RequestOrderByEditor(queryId : QueryId) 
    extends TWithQueryId

case class ErrorDatabaseAlreadyOpen(databaseId : DatabaseId)

case class DatabaseIds(names : List[DatabaseId])

case class ResponseAdditionalForeignKeys(databaseId : DatabaseId, keys : List[AdditionalForeignKey])
    extends TWithDatabaseId
