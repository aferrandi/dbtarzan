package dbtarzan.messages

import dbtarzan.db.{Composite, DatabaseId, SimpleDatabaseId}

object DatabaseIdUtil {
  def databaseIdText(databaseId: DatabaseId): String =
    databaseId.origin match {
      case Left(id) => id.databaseName
      case Right(id) => id.compositeName
    }

  def extractSimpleDatabaseIds(databaseId: DatabaseId, composite: Composite) : List[SimpleDatabaseId] = databaseId.origin match {
    case Left(id) => List(id)
    case Right(id) => composite.databaseIds
  }
}
