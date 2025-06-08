package dbtarzan.messages

import dbtarzan.db.*
import org.apache.pekko.actor.ActorRef
import dbtarzan.config.connections.ConnectionData

trait TWithDatabaseId { def databaseId : DatabaseId }

trait TWithQueryId { def queryId : QueryId }

trait TWithTableId { def tableId : TableId }

trait TWithDatabases { }
      
case class ResponseRows(queryId : QueryId, structure : DBTableStructure, rows : Rows)
    extends TWithQueryId

case class ErrorRows(queryId : QueryId,  ex: Exception) 
    extends TWithQueryId

case class ResponseOneRow(queryId : QueryId, structure : DBRowStructure, row : Row)
  extends TWithQueryId

case class ResponseTables(databaseId : DatabaseId, names: TableIds, dbActor : ActorRef)
    extends TWithDatabaseId

case class ResponseTablesByPattern(databaseId : DatabaseId, tabeIds: TableIds)
    extends TWithDatabaseId

case class ResponseCloseTables(databaseId : DatabaseId, ids : List[QueryId]) 
    extends TWithDatabaseId

case class ResponseSchemas(databaseId : DatabaseId, schemaIds: SchemaIds)
  extends TWithDatabaseId

case class ResponseColumns(tableId  : TableId, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithTableId

case class  ResponseColumnsForForeignKeys(tableId  : TableId, columns : Fields)
    extends TWithTableId

case class ResponsePrimaryKeys(queryId : QueryId, structure : DBTableStructure, keys : PrimaryKeys)
    extends TWithQueryId

case class ResponseForeignKeys(queryId : QueryId, structure : DBTableStructure, keys : ForeignKeys)
    extends TWithQueryId

case class ResponseForeignKeysByPatterns(queryId: QueryId, keys: ForeignKeys)
  extends TWithQueryId

case class ResponseIndexes(queryId : QueryId, indexes: Indexes)
  extends TWithQueryId

case class ResponseRowsNumber(queryId : QueryId, rowsNumber: Int)
  extends TWithQueryId

case class ResponseColumnsFollow(tableId: TableId,  follow : FollowKey, columns : Fields, queryAttributes : QueryAttributes) 
    extends TWithTableId

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

case class RequestOrderByField(queryId : QueryId, field : Field, direction: OrderByDirection)
    extends TWithQueryId

case class RequestOrderByEditor(queryId : QueryId) 
    extends TWithQueryId

case class ReloadQuery(queryId : QueryId, closeCurrentTab: Boolean)
    extends TWithQueryId

case class ResponseDatabasesByPattern(infos : List[DatabaseInfo])
  extends TWithDatabases
case class ErrorDatabaseAlreadyOpen(databaseId : DatabaseId)

case class DatabaseIds(ids : List[DatabaseId])

case class DatabaseInfos(infos : List[DatabaseInfo])
  extends TWithDatabases

case class CompositeIds(compositeIds: List[CompositeId])

case class ResponseVirtualForeignKeys(databaseId : DatabaseId, keys : List[VirtualalForeignKey])
    extends TWithDatabaseId

case class ResponseTestConnection(data : ConnectionData,  ex: Option[Exception])

case class ResponseSchemaExtraction(data : ConnectionData,  schemas: Option[SchemaNames],  ex: Option[Exception])


