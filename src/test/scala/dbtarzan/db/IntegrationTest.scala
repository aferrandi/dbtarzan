package dbtarzan.db

import org.scalatest.BeforeAndAfter

import java.sql.Connection
import java.sql.DriverManager
import dbtarzan.db.foreignkeys.{FKRow, ForeignKeyCriteria, ForeignKeyLoader}
import dbtarzan.db.basicmetadata.{MetadataColumnsLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader, MetadataTablesLoader}
import dbtarzan.db.loader.QueryLoader
import dbtarzan.db.sql.SqlBuilder
import dbtarzan.testutil.{FakeLogger, TestDatabaseIds}

import scala.concurrent.duration.*
import scala.language.postfixOps
import org.scalatest.flatspec.AnyFlatSpec

import scala.compiletime.uninitialized

class IntegrationTest extends AnyFlatSpec with BeforeAndAfter {
  var connection: Connection = uninitialized

  def productTableId: TableId = TestDatabaseIds.simpleTableId( "PRODUCT")

  def laptopTableId: TableId = TestDatabaseIds.simpleTableId("LAPTOP")

  def pcTableId: TableId = TestDatabaseIds.simpleTableId("PC")

  def printerTableId: TableId = TestDatabaseIds.simpleTableId("PRINTER")

  "tablenames" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataTablesLoader(DBDefinition(Some(schemaId), None), connection.getMetaData)
    val tableNames = metadataLoader.tableNames()
    assert(List("LAPTOP", "PC", "PRINTER", "PRODUCT" ) === tableNames.names)
  }


  private def schemaId = {
    SchemaId(TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, SchemaName("COMPUTER"))
  }

  "columnNames of LAPTOP" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataColumnsLoader(DBDefinition(Some(schemaId), None), connection.getMetaData, new FakeLogger())
    val columnNames = metadataLoader.columnNames("LAPTOP")
    assert(
      List(
        Field("CODE", FieldType.INT, "INTEGER [32,0]"),
        Field("MODEL", FieldType.STRING, "CHARACTER VARYING [50,0]"),
        Field("SPEED", FieldType.INT, "INTEGER [32,0]"),
        Field("RAM", FieldType.INT, "INTEGER [32,0]"),
        Field("HD", FieldType.FLOAT, "DOUBLE PRECISION [53,0]"),
        Field("PRICE", FieldType.FLOAT, "DOUBLE PRECISION [53,0] NULL"),
        Field("SCREEN",FieldType.INT, "INTEGER [32,0]")
        ) === columnNames.fields)
  }

  "schemaNames" should "give a list of the schemas in the database" in {
    val metadataLoader = new MetadataSchemasLoader(connection.getMetaData, new FakeLogger())
    val schemasNames = metadataLoader.schemasNames()
    assert(Set(SchemaName("INFORMATION_SCHEMA"), SchemaName("PUBLIC"), SchemaName("COMPUTER")) === schemasNames.toSet)
  }


  "primaryKeys of LAPTOP" should "give a sorted list of primary keys " in {
    val metadataLoader = new MetadataPrimaryKeysLoader(DBDefinition(Some(schemaId), None), connection.getMetaData, new FakeLogger())
    val primaryKeys = metadataLoader.primaryKeys("LAPTOP")
    assert(List(PrimaryKey("PK_LAPTOP", List("CODE"))) === primaryKeys.keys)
  }

  "foreignKeys of LAPTOP" should "give a list of foreign keys to PRODUCT" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, DBDefinition(Some(schemaId), None), new FakeLogger())
    val foreignKeys = foreignKeyLoader.foreignKeys(laptopTableId)
    assert(
      List(
        ForeignKey("FK_LAPTOP_PRODUCT", FieldsOnTable(laptopTableId, List("MODEL")), FieldsOnTable(productTableId, List("MODEL")), ForeignKeyDirection.STRAIGHT)
      ) 
      === foreignKeys.keys)
  }

  "foreignKeys of PRODUCT" should "give a list of foreign keys to LAPTOP,PC and PRINTER" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, TestDatabaseIds.databaseId, TestDatabaseIds.simpleDatabaseId, DBDefinition(Some(schemaId), None), new FakeLogger())
    val foreignKeys = foreignKeyLoader.foreignKeys(productTableId)
    assert(
      List(
        ForeignKey("FK_LAPTOP_PRODUCT", FieldsOnTable(productTableId, List("MODEL")), FieldsOnTable(laptopTableId, List("MODEL")), ForeignKeyDirection.TURNED),
        ForeignKey("FK_PC_PRODUCT", FieldsOnTable(productTableId, List("MODEL")), FieldsOnTable(pcTableId, List("MODEL")), ForeignKeyDirection.TURNED),
        ForeignKey("FK_PRINTER_PRODUCT", FieldsOnTable(productTableId, List("MODEL")), FieldsOnTable(printerTableId, List("MODEL")), ForeignKeyDirection.TURNED)
      ) 
      === foreignKeys.keys)
  }

  "query of PC" should "give the rows matching the where clause in the correct order" in {
    val structure = DBTableStructure(
        TableDescription("pc", None, None),
        Fields(
          List(FieldType.INT, FieldType.INT, FieldType.INT, FieldType.INT, FieldType.FLOAT, FieldType.STRING, FieldType.FLOAT).map(t => Field("x", t, ""))
          ),
        Some(
          ForeignKeyCriteria(List(FKRow(List(FieldWithValue("model", "1232")))), List(Field("model",  FieldType.STRING, "")))
          ),
        Some(Filter("speed > 450")),
        Some(OrderByFields(List(
          OrderByField(Field("model",  FieldType.STRING, ""), OrderByDirection.ASC)
          ))),
        QueryAttributes.none()
    )
    val sql = SqlBuilder.buildQuerySql(structure)
    var rows : Rows = Rows(List())
    new QueryLoader(connection, new FakeLogger()).query(sql, 500, 10 seconds, None, structure.columns, rs => rows = rs)
    assert(Rows(List(Row(List(1, 1232, 500, 64, 5.0, "12x", 600.0)), Row(List(7, 1232, 500, 32, 10.0, "12x", 400.0))))  === rows)
  }

  "query of PC" should "give the no more rows than the limit" in {
    val structure = DBTableStructure(
        TableDescription("pc", None, None),
        noFields(),
        None,
        None,
        None,
        QueryAttributes.none()
      )
    val sql = SqlBuilder.buildQuerySql(structure)
    var rows : Rows = Rows(List())
    new QueryLoader(connection, new FakeLogger()).query(sql, 3, 10 seconds, None, structure.columns, rs => rows = rs)
    assert(3  === rows.rows.length)
  }

  private def noFields() = Fields(List())

  before {
    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection( "jdbc:h2:mem:;INIT=runscript from 'classpath:sampledb/computer.sql'", "sa", "" )
  }

  after {
    connection.close()
  }
}