package dbtarzan.messages

import dbtarzan.db.TableId

/* identifies a table tab in the GUI, which is relative to the query that loads its rows.
Since there can potentially be several tabs based on the same table, we use a generated id to distinguish among them */
case class QueryId(tableId : TableId, uuid : String)

opaque type Jobid = Int

/* the id of atable in A job */
case class TableInJobId(tableId: TableId, jobId: Jobid)

case class QueryId(queryId : TableId, jobId: Jobid, uuid : String)


object IDGenerator {
	def queryId(tableId : TableId): QueryId = QueryId(tableId, java.util.UUID.randomUUID.toString)
}