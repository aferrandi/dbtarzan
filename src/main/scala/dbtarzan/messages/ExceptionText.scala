package dbtarzan.messages

import java.sql.SQLException

object ExceptionText {
  /* in case of error, gets the stacktrace */
  def extractWholeExceptionText(ex : Throwable) : String =
    exceptionData(ex) +
      " at:\n"+ ex.getStackTrace.mkString("\n") +
      Option(ex.getCause).map(c => "caused by:\n"+extractWholeExceptionText(c)).getOrElse("")

  private def exceptionData(ex : Throwable) : String =
    ex match {
      case sqex : SQLException => sqlExceptionRecursive(sqex)
      case _ => ex.getMessage
  }

  private def sqlExceptionText(sqex: SQLException) : String =
    sqex.getMessage+sqlExceptionAdditionalFields(sqex)

  private def sqlExceptionRecursive(sqex: SQLException): String =
    sqlExceptionText(sqex)+Option(sqex.getNextException).map(n => "\nnext exception:\n"+sqlExceptionRecursive(n)).getOrElse("")

  private def sqlExceptionAdditionalFields(sqex: SQLException) =
    " (Error code: " + sqex.getErrorCode + ", state: " + sqex.getSQLState + ")"

  /* in case of error, gets the stacktrace */
  def extractMessageText(ex : Throwable) : String =
    exceptionData(ex) + Option(ex.getCause).map(c => " "+extractMessageText(c)).getOrElse("")

}
