package dbtarzan.db

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import java.sql.Connection
import java.sql.DriverManager

class IntegrationTest extends FlatSpec with BeforeAndAfter {
  var connection: Connection = _


  "tablenames" should "give a sorted list of the table names" in {
    val metadataLoader = new BasicMetadataLoader(None, connection.getMetaData())
    val tableNames = metadataLoader.tableNames()
  	assert(List("LAPTOP", "PC", "PRINTER", "PRODUCT" ) === tableNames.tableNames)
  }
  
  "columnNames of LAPTOP" should "give a sorted list of the table names" in {
    val metadataLoader = new BasicMetadataLoader(None, connection.getMetaData())
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

  "primaryKeys of LAPTOP" should "give a sorted list of primary keys " in {
    val metadataLoader = new BasicMetadataLoader(None, connection.getMetaData())
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

  before {
    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection( "jdbc:h2:mem:;INIT=runscript from 'classpath:sampledb/computer.sql';", "sa", "" )
  }

  after {
    connection.close()
  }
}