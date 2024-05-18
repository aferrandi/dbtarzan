
package dbtarzan.config.connections


import dbtarzan.config.password.Password
import org.scalatest.flatspec.AnyFlatSpec
import dbtarzan.db.SimpleDatabaseId

class ConnectionsConfigTest extends AnyFlatSpec {

  "getting connection with existing 1 name" should "return the connection" in {
    val config = new ConnectionsDataMap(List(
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Some(Password("malagodi")),None, None, None, None, None, None),
        ConnectionData("mysql.jar", "mysql", "DriverMysql", "jdbc://mysql", None, "arturo", Some(Password("fedele")),None, None, None, None, None, None)
      ))
    val data = config.connectionDataFor(SimpleDatabaseId("oracle"))
    assert("giovanni" === data.user)
  }

  "getting connection with non existing name" should "give an exception" in {
    val config = new ConnectionsDataMap(List[ConnectionData]())
    intercept[Exception] {
      config.connectionDataFor(SimpleDatabaseId("oracle"))
    }
  }
  "getting connection with existing 2 names" should "give an exception" in {
    val config = new ConnectionsDataMap(List(
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "giovanni", Some(Password("malagodi")),  None, None, None, None, None, None),
        ConnectionData("oracle.jar", "oracle", "DriverOracle", "jdbc://oracle", None, "carlo", Some(Password("sigismondi")),  None, None, None, None, None, None)
      ))
    intercept[Exception] {
      config.connectionDataFor(SimpleDatabaseId("oracle"))
    }
  }

}