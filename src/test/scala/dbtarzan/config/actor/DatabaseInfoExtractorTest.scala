package dbtarzan.config.actor

import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap}
import dbtarzan.config.password.Password
import dbtarzan.db.{Composite, CompositeId, DatabaseId, SimpleDatabaseId}
import dbtarzan.messages.{DatabaseIdUtil, ResponseDatabasesByPattern}
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable.HashSet

class DatabaseInfoExtractorTest extends AnyFlatSpec {
  "databases included completely in composites" should "not appear in databases infos" in {
    val currentComposites: List[Composite] = buldComposites
    val connectionData: ConnectionsDataMap = buldConnections

    val infos = DatabaseInfoExtractor.extractDatabaseInfos(currentComposites, connectionData)
    val ids = infos.infos.map(info => DatabaseIdUtil.databaseIdFromInfo(info))
    assert(3 === ids.size)
    assert(ids.contains(DatabaseId(Right(CompositeId("Orapost")))))
    assert(ids.contains(DatabaseId(Right(CompositeId("oramy")))))
    assert(ids.contains(DatabaseId(Left(SimpleDatabaseId("postgreSql")))))
  }

  private def buldConnections = {
    val connectionData = new ConnectionsDataMap(List(
      ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Some(Password("malagodi")), None, None, None, None, None, None, None),
      ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", Some(Password("fedele")),None, None, None, None, None, None, None),
      ConnectionData("postgresql.jar", "postgreSql", "DriverPostgres", "jdbc://postgresql", None, "gianni", Some(Password("boncompagni")), None, None, None, None, None, None, None)
    ))
    connectionData
  }

  private def buldComposites = {
    val currentComposites = List(
      Composite(CompositeId("Orapost"), List(SimpleDatabaseId("oracle"), SimpleDatabaseId("postgreSql")), true),
      Composite(CompositeId("oramy"), List(SimpleDatabaseId("oracle"), SimpleDatabaseId("mysql")), false)
    )
    currentComposites
  }

  "getting the infos matching a pattern" should "work and is case insensistive" in {
    val currentComposites: List[Composite] = buldComposites
    val connectionData: ConnectionsDataMap = buldConnections

    containsNames(DatabaseInfoExtractor.extractDatabaseInfosByPattern(currentComposites, connectionData, "ORA"), Set("oracle", "Orapost", "oramy"))
    containsNames(DatabaseInfoExtractor.extractDatabaseInfosByPattern(currentComposites, connectionData, "sQL"), Set("mysql", "postgreSql", "Orapost", "oramy"))
    assert(DatabaseInfoExtractor.extractDatabaseInfosByPattern(List.empty, ConnectionsDataMap(List.empty), "sQL").infos.isEmpty)
  }

  private def containsNames(infos: ResponseDatabasesByPattern, contained: Set[String]): Any = {
    val ids = infos.infos.map(info => DatabaseIdUtil.databaseInfoText(info))
    assert(contained.size === ids.size)
    assert(contained.subsetOf(ids.toSet))
  }
}
