package dbtarzan.db

import java.net.{URL, URLClassLoader}
import java.sql.{Driver, DriverManager}

case class DriverSpec(jar : String, driver : String)

object RegisterDriver {
  def registerDriver(spec: DriverSpec) : Unit = {
      // Load the driver
      val url = new URL("jar:file:" + spec.jar + "!/")
      val classLoader = new URLClassLoader(Array(url))
      val driverClass = Class.forName(spec.driver, true, classLoader)
      val driverInstance = driverClass.newInstance().asInstanceOf[Driver]
      DriverManager.registerDriver(new DriverShim(driverInstance))
  }
}
