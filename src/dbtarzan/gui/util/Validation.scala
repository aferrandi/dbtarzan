

package dbtarzan.gui.util

import java.net.URL
import scala.util.Try

object Validation {
	def isValidURL(urlStr : String) = Try(new URL(urlStr)).isSuccess
	def containsWhtitespace(txt : String) = txt.exists(_.isWhitespace)
}