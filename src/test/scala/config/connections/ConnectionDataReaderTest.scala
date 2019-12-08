package dbtarzan.config.connections

import org.scalatest.FlatSpec
import spray.json._
import dbtarzan.config.password.Password

class ConnectionDataReaderTest extends FlatSpec {
   import ConnectionDataJsonProtocol._

  "A list of connections" should "be parseable" in {
  	val values= List(
      ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Password("malagodi"), Some(false), None, None, None, None, None),
  		ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", Password("fedele"), None, None, None, None, None, None)
  		)
  	val json = values.toJson.prettyPrint
  	println(json)
  	val extracted = ConnectionDataReader.parseText(json)
  	assert(extracted === values.reverse)
  }
}