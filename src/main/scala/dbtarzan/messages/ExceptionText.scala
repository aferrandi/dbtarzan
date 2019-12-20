package dbtarzan.messages

import java.sql.SQLException

object ExceptionText {
  /* in case of error, gets the stacktrace */
  def extractWholeExceptionText(ex : Throwable) : String =
    ex.getMessage + sqlExceptionData(ex) +
      " at:\n"+ ex.getStackTrace.mkString("\n") +
      Option(ex.getCause).map(c => "caused by:\n"+extractWholeExceptionText(c)).getOrElse("")

  private def sqlExceptionData(ex : Throwable) : String =
    ex match {
      case sqex : SQLException => " (Error code: "+sqex.getNextException + ", state: " + sqex.getSQLState+")"
      case _ => ""
  }

  /* in case of error, gets the stacktrace */
  def extractMessageText(ex : Throwable) : String =
    ex.getMessage  + sqlExceptionData(ex) + Option(ex.getCause).map(c => " "+extractMessageText(c)).getOrElse("")

}
