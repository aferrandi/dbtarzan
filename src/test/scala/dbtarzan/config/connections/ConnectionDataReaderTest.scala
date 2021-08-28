package dbtarzan.config.connections

import dbtarzan.config.password.Password
import dbtarzan.db.IdentifierDelimitersValues
import org.scalatest.FlatSpec
import spray.json._

class ConnectionDataReaderTest extends FlatSpec {
   import ConnectionDataJsonProtocol._
  "A list of connections" should "be parseable" in {
  	val values= List(
      ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Password("malagodi"), Some(false), None, None, None, None, None, None),
  		ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", Password("fedele"), None, Some(2), Some(IdentifierDelimitersValues.doubleQuotes), Some(300), Some(20), None, Some("catalog"))
  		)
  	val json = values.toJson.prettyPrint
  	println(json)
  	val extracted = ConnectionDataReader.parseText(json)
  	assert(extracted === values.reverse)
  }
}