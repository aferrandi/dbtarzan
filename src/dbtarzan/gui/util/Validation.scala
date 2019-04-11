package dbtarzan.gui.util

object Validation {
	def isValidJdbcURL(urlStr : String) = urlStr.toLowerCase.startsWith("jdbc:")
	def containsWhitespace(txt : String) = txt.exists(_.isWhitespace)
}