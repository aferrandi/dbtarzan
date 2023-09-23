package dbtarzan.gui.util

import java.time.format.DateTimeFormatter

object DateUtils {
  def timeFormatter(): DateTimeFormatter = {
    DateTimeFormatter.ofPattern("HH:mm:ss")
  }
}
