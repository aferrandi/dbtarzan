package dbtarzan.db

import java.net.{URL, URLClassLoader}
import java.sql.{Driver, DriverManager}

import scala.collection.mutable

case class DriverSpec(jar : String, driver : String)

object RegisterDriver {
  private def registerDriver(spec: DriverSpec) : Unit = {
      // Load the driver
      val url = new URL("jar:file:" + spec.jar + "!/")
      val classLoader = new URLClassLoader(Array(url))
      val driverClass = Class.forName(spec.driver, true, classLoader)
      val driverInstance = driverClass.getDeclaredConstructor().newInstance().asInstanceOf[Driver]
      DriverManager.registerDriver(new DriverShim(driverInstance))
  }
}

class RegisterDriver {
  /* to prevent registering a driver twice. It must be kept in the same thread (actor) */
  private val alreadyRegistered = mutable.HashSet[DriverSpec]()

  def registerDriverIfNeeded(spec: DriverSpec) : Unit = {
    if(!alreadyRegistered.contains(spec)) {
      RegisterDriver.registerDriver(spec)
      alreadyRegistered += spec
    }
  }
}