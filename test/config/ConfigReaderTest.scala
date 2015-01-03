
package dbtarzan.config

import org.scalatest.FlatSpec
import spray.json._


class ConfigReaderTest extends FlatSpec {
   import ConnectionDataJsonProtocol._

  "A list of connections" should "be parseable" in {
  	val values= List(ConnectionData("oracle", "DriverOracle", "jdbc://oracle", "giovanni", "malagodi"),
  		ConnectionData("mysql", "DriverMysql", "jdbc://mysql", "arturo", "fedele")
  		)
  	val json = values.toJson.prettyPrint
  	println(json)
  	val extracted = ConfigReader.read(json)
  	assert(extracted === values)
  }

}