package dbtarzan.testutil

import dbtarzan.db.{DatabaseId, SimpleDatabaseId, TableId}

object TestDatabaseIds {
  def simpleDatabaseId: SimpleDatabaseId = SimpleDatabaseId("database")

  def databaseId: DatabaseId = DatabaseId(Left(simpleDatabaseId))

  def simpleTableId(tableName: String) : TableId = TableId(databaseId, simpleDatabaseId, tableName)
}
