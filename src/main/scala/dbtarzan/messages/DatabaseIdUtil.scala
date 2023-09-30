package dbtarzan.messages

import dbtarzan.db.{Composite, DatabaseId, DatabaseInfo, SimpleDatabaseId}

object DatabaseIdUtil {
  def databaseIdText(databaseId: DatabaseId): String =
    databaseId.origin match {
      case Left(id) => id.databaseName
      case Right(id) => id.compositeName
    }

  def databaseInfoText(databaseInfo: DatabaseInfo): String =
    databaseInfo.origin match {
      case Left(info) => info.simpleDatabaseId.databaseName
      case Right(info) => info.compositeId.compositeName
    }

  def databaseIdFromInfo(databaseInfo: DatabaseInfo): DatabaseId =
    DatabaseId(databaseInfo.origin match {
      case Left(info) => Left(info.simpleDatabaseId)
      case Right(info) => Right(info.compositeId)
    })

  def extractSimpleDatabaseIds(databaseId: DatabaseId, composite: Composite) : List[SimpleDatabaseId] = databaseId.origin match {
    case Left(id) => List(id)
    case Right(_) => composite.databaseIds
  }

  def extractSimpleDatabasesThatNeedLoginPassword(databaseInfo: DatabaseInfo) : List[SimpleDatabaseId] = {
    databaseInfo.origin match
      case Left(simpleDatabaseInfo) => if(simpleDatabaseInfo.needsPassword) List(simpleDatabaseInfo.simpleDatabaseId) else List.empty
      case Right(composite) => composite.databaseInfos.filter(_.needsPassword).map(_.simpleDatabaseId)
  }
}
