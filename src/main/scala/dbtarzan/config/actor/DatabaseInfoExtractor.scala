package dbtarzan.config.actor

import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap, DatabaseInfoFromConfig}
import dbtarzan.db.{Composite, CompositeId}
import dbtarzan.messages.{DatabaseInfos, ResponseDatabasesByPattern}

object DatabaseInfoExtractor {
  def extractDatabaseInfos(currentComposites : List[Composite], connectionsDataMap: ConnectionsDataMap): DatabaseInfos = {
    val connectionsDataRemaining = connectionsNotInComposites(currentComposites, connectionsDataMap)
    val connectionInfos = DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionsDataRemaining)
    val compositeInfos = DatabaseInfoFromConfig.extractCompositeInfos(currentComposites, connectionsDataMap.connectionDataFor)
    DatabaseInfos(connectionInfos ++ compositeInfos)
  }

  private def connectionsNotInComposites(currentComposites : List[Composite], connectionsDataMap: ConnectionsDataMap): List[ConnectionData] = {
    val connectionsToRemove = currentComposites.filter(co => !co.showAlsoIndividualDatabases).flatMap(co => co.databaseIds).map(id => id.databaseName).toSet
    val connectionsDataRemaining = connectionsDataMap.connectionDatas.filter(cd => !connectionsToRemove.contains(cd.name))
    connectionsDataRemaining
  }

  def extractDatabaseInfosByPattern(currentComposites: List[Composite], connectionsDataMap: ConnectionsDataMap, pattern: String): ResponseDatabasesByPattern = {
    val patternLowerCase = pattern.toLowerCase()
    val connectionInfos = DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionsDataMap.connectionDatas.filter(v => v.name.toLowerCase().contains(pattern)))
    val compositeInfos = DatabaseInfoFromConfig.extractCompositeInfos(currentComposites.filter(v => v.compositeId.compositeName.toLowerCase().contains(pattern)), connectionsDataMap.connectionDataFor)
    ResponseDatabasesByPattern(connectionInfos ++ compositeInfos)
  }
}
