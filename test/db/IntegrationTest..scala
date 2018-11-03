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
  
  before {
    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection( "jdbc:h2:mem:;INIT=runscript from 'classpath:sampledb/computer.sql';", "sa", "" )
  }

  after {
    connection.close()
  }
}