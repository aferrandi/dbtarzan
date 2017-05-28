
package dbtarzan.config

import org.scalatest.FlatSpec


class ConnectionsConfigTest extends FlatSpec {

  "getting connection with existing 1 name" should "return the connection" in {
    val config = new ConnectionsConfig(List(
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", "malagodi", Some(false), None, None, None),
        ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", "fedele", None, None, None, None)
      ))
    val data = config.connect("oracle")
  	assert("giovanni" === data.user)
  }

  "getting connection with non existing name" should "give an exception" in {
    val config = new ConnectionsConfig(List[ConnectionData]())
    intercept[Exception] {
      config.connect("oracle")
    }
  }
  "getting connection with existing 2 names" should "give an exception" in {
    val config = new ConnectionsConfig(List(
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", "malagodi", Some(false), None, None, None),
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "carlo", "sigismondi", Some(false), None, None, None)
      ))
    intercept[Exception] {
      config.connect("oracle")
    }
  }

}