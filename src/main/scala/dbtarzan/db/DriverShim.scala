package dbtarzan.db

/**
	Needed to load database drivers dynamically.
	See http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
*/
class DriverShim(driver : java.sql.Driver) extends java.sql.Driver {
  def acceptsURL(url: String): Boolean = driver.acceptsURL(url)
  def connect(url: String, info: java.util.Properties): java.sql.Connection = driver.connect(url, info)
  def getMajorVersion(): Int = driver.getMajorVersion()
  def getMinorVersion(): Int = driver.getMinorVersion()
  def getParentLogger(): java.util.logging.Logger = driver.getParentLogger()
  def getPropertyInfo(url: String,info: java.util.Properties): Array[java.sql.DriverPropertyInfo] = driver.getPropertyInfo(url, info)
  def jdbcCompliant(): Boolean = driver.jdbcCompliant()
}