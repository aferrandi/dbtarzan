package dbtarzan.gui.util

object Validation {
	def isValidJdbcURL(urlStr : String): Boolean = urlStr.toLowerCase.startsWith("jdbc:")
	def containsWhitespace(txt : String): Boolean = txt.exists(_.isWhitespace)
  def isMoreThanOrNone(value: Option[Int], min: Int): Boolean = value.forall(_ >= min)
  def isIdentifier(txt : String): Boolean = txt.matches("[a-zA-Z][_\\-a-zA-Z0-9]*")
}