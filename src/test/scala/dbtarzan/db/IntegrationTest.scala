package dbtarzan.db

import org.scalatest.BeforeAndAfter

import java.sql.Connection
import java.sql.DriverManager
import dbtarzan.db.foreignkeys.{FKRow, ForeignKeyCriteria, ForeignKeyLoader}
import dbtarzan.db.basicmetadata.{MetadataColumnsLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader, MetadataTablesLoader}
import dbtarzan.localization.English
import dbtarzan.testutil.FakeLogger

import scala.concurrent.duration._
import scala.language.postfixOps
import org.scalatest.flatspec.AnyFlatSpec

class IntegrationTest extends AnyFlatSpec with BeforeAndAfter {
  var connection: Connection = _


  "tablenames" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataTablesLoader(DBDefinition(None, None), connection.getMetaData)
    val tableNames = metadataLoader.tableNames()
  	assert(List("LAPTOP", "PC", "PRINTER", "PRODUCT" ) === tableNames.tableIds)
  }


  "columnNames of LAPTOP" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataColumnsLoader(DBDefinition(None, None), connection.getMetaData, new FakeLogger())
    val columnNames = metadataLoader.columnNames("LAPTOP")
  	assert(
      List(
        Field("CODE", FieldType.INT, "INTEGER [10,0]"), 
        Field("MODEL", FieldType.STRING, "VARCHAR [50,0]"), 
        Field("SPEED", FieldType.INT, "INTEGER [10,0]"), 
        Field("RAM", FieldType.INT, "INTEGER [10,0]"), 
        Field("HD", FieldType.FLOAT, "DOUBLE [17,0]"),
        Field("PRICE", FieldType.FLOAT, "DOUBLE [17,0] NULL"), 
        Field("SCREEN",FieldType.INT, "INTEGER [10,0]")
        ) === columnNames.fields)
  }

  "schemaNames" should "give a list of the schemas in the database" in {
    val metadataLoader = new MetadataSchemasLoader(connection.getMetaData, new FakeLogger())
    val schemasNames = metadataLoader.schemasNames()
  	assert(List(Schema("INFORMATION_SCHEMA"), Schema("PUBLIC")) === schemasNames.schemas)
  }


  "tablesByPattern" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataTablesLoader(DBDefinition(None, None), connection.getMetaData)
    val tableNames = metadataLoader.tablesByPattern("PRI")
  	assert(List("LAPTOP", "PC", "PRINTER") === tableNames.tableIds)
  }


  "primaryKeys of LAPTOP" should "give a sorted list of primary keys " in {
    val metadataLoader = new MetadataPrimaryKeysLoader(DBDefinition(None, None), connection.getMetaData, new FakeLogger())
    val primaryKeys = metadataLoader.primaryKeys("LAPTOP")
  	assert(List(PrimaryKey("PK_LAPTOP", List("CODE"))) === primaryKeys.keys)
  }

 "foreignKeys of LAPTOP" should "give a list of foreign keys to PRODUCT" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, DBDefinition(None, None), new English(), new FakeLogger())
    val foreignKeys = foreignKeyLoader.foreignKeys("LAPTOP")
  	assert(
      List(
        ForeignKey("FK_LAPTOP_PRODUCT", FieldsOnTable("LAPTOP", List("MODEL")), FieldsOnTable("PRODUCT", List("MODEL")), ForeignKeyDirection.STRAIGHT)
      ) 
      === foreignKeys.keys)
  }

   "foreignKeys of PRODUCT" should "give a list of foreign keys to LAPTOP,PC and PRINTER" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, DBDefinition(None, None), new English(), new FakeLogger())
    val foreignKeys = foreignKeyLoader.foreignKeys("PRODUCT")
  	assert(
      List(
        ForeignKey("FK_LAPTOP_PRODUCT", FieldsOnTable("PRODUCT", List("MODEL")), FieldsOnTable("LAPTOP", List("MODEL")), ForeignKeyDirection.TURNED),
        ForeignKey("FK_PC_PRODUCT", FieldsOnTable("PRODUCT", List("MODEL")), FieldsOnTable("PC", List("MODEL")), ForeignKeyDirection.TURNED),
        ForeignKey("FK_PRINTER_PRODUCT", FieldsOnTable("PRODUCT", List("MODEL")), FieldsOnTable("PRINTER", List("MODEL")), ForeignKeyDirection.TURNED)
      ) 
      === foreignKeys.keys)
  }

  "query of PC" should "give the rows matching the where clause in the correct order" in {
    val structure = DBTableStructure(
        TableDescription("pc", None, None),
        noFields(),
        Some(
          ForeignKeyCriteria(List(FKRow(List(FieldWithValue("model", "1232")))), List(Field("model",  FieldType.STRING, "")))
          ),
        Some(Filter("speed > 450")),
        Some(OrderByFields(List(
          OrderByField(Field("model",  FieldType.STRING, ""), OrderByDirection.ASC)
          ))),
        QueryAttributes.none()
    )
    val sql = SqlBuilder.buildSql(structure)
    var rows : Rows = Rows(List())
    new QueryLoader(connection, new FakeLogger()).query(sql, 500, 10 seconds, None, rs => rows = rs)
    assert(Rows(List(Row(List("1", "1232", "500", "64", "5.0", "12x", "600.0")), Row(List("7", "1232", "500", "32", "10.0", "12x", "400.0"))))  === rows)
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
    val sql = SqlBuilder.buildSql(structure)
    var rows : Rows = Rows(List())
    new QueryLoader(connection, new FakeLogger()).query(sql, 3, 10 seconds, None, rs => rows = rs)
    assert(3  === rows.rows.length)
  }

  private def noFields() = Fields(List())

  before {
    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection( "jdbc:h2:mem:;INIT=runscript from 'classpath:sampledb/computer.sql';", "sa", "" )
  }

  after {
    connection.close()
  }
}