package dbtarzan.messages

import org.apache.pekko.actor.ActorRef
import dbtarzan.db.{AdditionalForeignKey, Composite, DBRowStructure, DBTableStructure, DatabaseId, FollowKey, TableId}


case class OriginalQuery(queryId : QueryId, closeCurrentTab : Boolean)

case class QueryRows(queryId : QueryId, original : Option[OriginalQuery], structure : DBTableStructure)

case class QueryOneRow(queryId : QueryId, structure : DBRowStructure)

case class QueryTables(databaseId : DatabaseId, dbActor : ActorRef)

case class QueryTablesByPattern(databaseId : DatabaseId, pattern: String) 

case class QueryColumns(tableId: TableId) 

case class QueryColumnsForForeignKeys(tableId: TableId)

case class QueryColumnsFollow(tableId: TableId, follow : FollowKey) 

case class QueryPrimaryKeys(queryId : QueryId, structure : DBTableStructure)

case class QueryForeignKeys(queryId : QueryId, structure : DBTableStructure)

case class QueryIndexes(queryId : QueryId)

case class QuerySchemas(databaseId : DatabaseId)

case class QueryReset(databaseId : DatabaseId) 

case class UpdateAdditionalForeignKeys(databaseId : DatabaseId, keys : List[AdditionalForeignKey])

case class RequestAdditionalForeignKeys(databaseId : DatabaseId)

case class Composites(composites: List[Composite])
