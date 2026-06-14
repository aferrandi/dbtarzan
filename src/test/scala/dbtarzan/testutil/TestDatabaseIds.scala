package dbtarzan.testutil

import dbtarzan.db.{DatabaseId, SimpleDatabaseId, TableId, JobId}
import dbtarzan.messages.TableInJobId

object TestDatabaseIds {
  def simpleDatabaseId: SimpleDatabaseId = SimpleDatabaseId("database")

  def databaseId: DatabaseId = DatabaseId(Left(simpleDatabaseId))

  def simpleTableId(tableName: String) : TableId = TableId(databaseId, simpleDatabaseId, tableName)

  def simpleTableInJobId(jobId: Int, tableName: String) : TableInJobId = TableInJobId(TestDatabaseIds.simpleTableId(tableName), JobId(jobId))
}
