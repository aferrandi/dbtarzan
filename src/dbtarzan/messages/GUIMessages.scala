package dbtarzan.messages

import dbtarzan.db.{Rows, TableNames, Fields, ForeignKeys, FollowKey, QueryAttributes, PrimaryKeys, DatabaseId}
import akka.actor.ActorRef

trait TWithDatabaseId { def databaseId : DatabaseId }

trait TWithTableId { def tableId : TableId }

      
case class ResponseRows(tableId : TableId, rows: Rows) 
    extends TWithTableId

case class ResponseTables(databaseId : DatabaseId, names: TableNames) 
    extends TWithDatabaseId

case class ResponseCloseTables(databaseId : DatabaseId, ids : List[TableId]) 
    extends TWithDatabaseId

case class ResponseColumns(databaseId: DatabaseId, tableName : String, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithDatabaseId

case class ResponsePrimaryKeys(tableId : TableId, keys : PrimaryKeys) 
    extends TWithTableId

case class ResponseForeignKeys(tableId : TableId, keys : ForeignKeys) 
    extends TWithTableId

case class ResponseColumnsFollow(databaseId: DatabaseId, tableName : String,  follow : FollowKey, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithDatabaseId

case class ErrorDatabaseAlreadyOpen(databaseId : DatabaseId) extends TWithDatabaseId

case class ResponseDatabase(databaseId : DatabaseId, dbActor : ActorRef) extends TWithDatabaseId

case class ResponseCloseDatabase(databaseId : DatabaseId) extends TWithDatabaseId

case class RequestRemovalTabsAfter(tableId : TableId) extends TWithTableId

case class RequestRemovalTabsBefore(tableId : TableId) extends TWithTableId

case class RequestRemovalAllTabs(databaseId : DatabaseId) extends TWithDatabaseId

case class DatabaseIds(names : List[DatabaseId])

case class CopySelectionToClipboard(tableId : TableId, includeHeaders : Boolean) extends TWithTableId

case class CopySQLToClipboard(tableId : TableId) extends TWithTableId

case class CheckAllTableRows(tableId : TableId) extends TWithTableId

case class CheckNoTableRows(tableId : TableId) extends TWithTableId

case class SwitchRowDetails(tableId : TableId) extends TWithTableId


