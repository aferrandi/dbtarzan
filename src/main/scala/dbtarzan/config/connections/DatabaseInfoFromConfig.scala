package dbtarzan.config.connections

import dbtarzan.db.{Composite, CompositeInfo, DatabaseInfo, SimpleDatabaseId, SimpleDatabaseInfo}

object DatabaseInfoFromConfig {
  def extractSimpleDatabaseInfos(connectionDatas: List[ConnectionData]): List[DatabaseInfo] = {
    connectionDatas
      .map(c => SimpleDatabaseInfo(SimpleDatabaseId(c.name), c.password.isEmpty))
      .map(id => DatabaseInfo(Left(id)))
  }

  def extractCompositeInfos(composites: List[Composite], connectionDataFor: SimpleDatabaseId => ConnectionData): List[DatabaseInfo] = {
    composites.map(c => DatabaseInfo(
      Right(CompositeInfo(
        c.compositeId,
        c.databaseIds.map(id => SimpleDatabaseInfo(id, connectionDataFor(id).password.isEmpty))))))
  }

}
