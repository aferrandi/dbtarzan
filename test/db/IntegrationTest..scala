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

  before {
    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection( "jdbc:h2:mem:;INIT=runscript from 'classpath:sampledb/computer.sql';", "sa", "" )
  }

  after {
    connection.close()
  }
}