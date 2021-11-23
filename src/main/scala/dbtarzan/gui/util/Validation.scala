package dbtarzan.gui.util

object Validation {
	def isValidJdbcURL(urlStr : String): Boolean = urlStr.toLowerCase.startsWith("jdbc:")
	def containsWhitespace(txt : String): Boolean = txt.exists(_.isWhitespace)
  def isMoreThan(value: Option[Int], min: Int): Boolean = value.forall(_ >= min)
  def isDigitsOrLetters(txt : String): Boolean = txt.forall(c => c.isLetterOrDigit)
}