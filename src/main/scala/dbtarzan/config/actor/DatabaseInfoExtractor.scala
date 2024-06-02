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
    val connectionsToRemove = extractConnectionsToRemove(currentComposites)
    val connectionsDataRemaining = connectionsDataMap.connectionDatas.filter(cd => !connectionsToRemove.contains(cd.name))
    connectionsDataRemaining
  }

  private def extractConnectionsToRemove(currentComposites: List[Composite]): Set[String] = {
    val connectionsToRemoveIfNoCompositePreserves = connectionsIncludedInComposites(currentComposites, false)
    val connectionsToPreserveAnyway = connectionsIncludedInComposites(currentComposites, true)
    connectionsToRemoveIfNoCompositePreserves.diff(connectionsToPreserveAnyway)
  }

  private def connectionsIncludedInComposites(currentComposites: List[Composite], connectionsShowAlsoIndividualDatabases: Boolean) = {
    currentComposites.filter(co => co.showAlsoIndividualDatabases == connectionsShowAlsoIndividualDatabases)
      .flatMap(co => co.databaseIds).map(id => id.databaseName).toSet
  }

  def extractDatabaseInfosByPattern(currentComposites: List[Composite], connectionsDataMap: ConnectionsDataMap, pattern: String): ResponseDatabasesByPattern = {
    val patternLowerCase = pattern.toLowerCase()
    def containsPattern(text: String) =
      text.toLowerCase().contains(patternLowerCase)

    val connectionInfos = DatabaseInfoFromConfig.extractSimpleDatabaseInfos(connectionsDataMap.connectionDatas.filter(v => containsPattern(v.name)))
    val compositeInfos = DatabaseInfoFromConfig.extractCompositeInfos(currentComposites.filter(v =>
      containsPattern(v.compositeId.compositeName) || v.databaseIds.exists(d => containsPattern(d.databaseName))
    ), connectionsDataMap.connectionDataFor)
    ResponseDatabasesByPattern(connectionInfos ++ compositeInfos)
  }
}
