package dbtarzan.db

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import java.sql.Connection
import java.sql.DriverManager
import dbtarzan.db.basicmetadata.{MetadataTablesLoader, MetadataColumnsLoader, MetadataPrimaryKeysLoader, MetadataSchemasLoader} 

class IntegrationTest extends FlatSpec with BeforeAndAfter {
  var connection: Connection = _


  "tablenames" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataTablesLoader(None, connection.getMetaData())
    val tableNames = metadataLoader.tableNames()
  	assert(List("LAPTOP", "PC", "PRINTER", "PRODUCT" ) === tableNames.tableNames)
  }


  "columnNames of LAPTOP" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataColumnsLoader(None, connection.getMetaData())
    val columnNames = metadataLoader.columnNames("LAPTOP")
  	assert(
      List(
        Field("CODE", FieldType.INT), 
        Field("MODEL", FieldType.STRING), 
        Field("SPEED", FieldType.INT), 
        Field("RAM", FieldType.INT), 
        Field("HD", FieldType.FLOAT),
        Field("PRICE", FieldType.FLOAT), 
        Field("SCREEN",FieldType.INT)
        ) === columnNames.fields)
  }

  "schemaNames" should "give a list of the schemas in the database" in {
    val metadataLoader = new MetadataSchemasLoader(connection.getMetaData())
    val schemasNames = metadataLoader.schemasNames()
  	assert(List(Schema("INFORMATION_SCHEMA"), Schema("PUBLIC")) === schemasNames.schemas)
  }


  "tablesByPattern" should "give a sorted list of the table names" in {
    val metadataLoader = new MetadataTablesLoader(None, connection.getMetaData())
    val tableNames = metadataLoader.tablesByPattern("PRI")
  	assert(List("LAPTOP", "PC", "PRINTER") === tableNames.tableNames)
  }


  "primaryKeys of LAPTOP" should "give a sorted list of primary keys " in {
    val metadataLoader = new MetadataPrimaryKeysLoader(None, connection.getMetaData())
    val primaryKeys = metadataLoader.primaryKeys("LAPTOP")
  	assert(List(PrimaryKey("PK_LAPTOP", List("CODE"))) === primaryKeys.keys)
  }

 "foreignKeys of LAPTOP" should "give a list of foreign keys to PRODUCT" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, None)
    val foreignKeys = foreignKeyLoader.foreignKeys("LAPTOP")
  	assert(
      List(
        ForeignKey("FK_LAPTOP_PRODUCT", FieldsOnTable("LAPTOP", List("MODEL")), FieldsOnTable("PRODUCT", List("MODEL")), ForeignKeyDirection.STRAIGHT)
      ) 
      === foreignKeys.keys)
  }

   "foreignKeys of PRODUCT" should "give a list of foreign keys to LAPTOP,PC and PRINTER" in {
    val foreignKeyLoader = new ForeignKeyLoader(connection, None)
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
          ForeignKeyCriteria(List(FKRow(List(FieldWithValue("model", "1232")))), List(Field("model",  FieldType.STRING)))
          ),
        Some(Filter("speed > 450")),
        Some(OrderByFields(List(
          OrderByField(Field("model",  FieldType.STRING), OrderByDirection.ASC)
          ))),
        QueryAttributes.none()
    )
    val sql = SqlBuilder.buildSql(structure)
    var rows : Rows = Rows(List())
    new QueryLoader(connection).query(sql, 500, rs => rows = rs)
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
    new QueryLoader(connection).query(sql, 3, rs => rows = rs)
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