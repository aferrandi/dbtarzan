package dbtarzan.config.actor

import dbtarzan.config.connections.{ConnectionData, ConnectionsDataMap}
import dbtarzan.config.password.Password
import dbtarzan.db.{Composite, CompositeId, DatabaseId, SimpleDatabaseId}
import dbtarzan.messages.DatabaseIdUtil
import org.scalatest.flatspec.AnyFlatSpec

class DatabaseInfoExtractorTest extends AnyFlatSpec {
  "databases included completely in composites" should "not appear in databases infos" in {
    val currentComposites = List(
      Composite(CompositeId("orapost"), List(SimpleDatabaseId("oracle"), SimpleDatabaseId("postgresql")), true),
      Composite(CompositeId("oramy"), List(SimpleDatabaseId("oracle"), SimpleDatabaseId("mysql")), false)
    )
    val connectionData = new ConnectionsDataMap(List(
      ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Some(Password("malagodi")), Some(false), None, None, None, None, None, None),
      ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", Some(Password("fedele")), None, None, None, None, None, None, None),
      ConnectionData("postgresql.jar", "postgresql", "DriverPostgres", "jdbc://postgresqu", None, "gianni", Some(Password("boncompagni")), None, None, None, None, None, None, None)
    ))

    val infos = DatabaseInfoExtractor.extractDatabaseInfos(currentComposites, connectionData)
    val ids = infos.infos.map(info => DatabaseIdUtil.databaseIdFromInfo(info))
    assert(3 === ids.size)
    assert(ids.contains(DatabaseId(Right(CompositeId("orapost")))))
    assert(ids.contains(DatabaseId(Right(CompositeId("oramy")))))
    assert(ids.contains(DatabaseId(Left(SimpleDatabaseId("postgresql")))))
  }
}
