package dbtarzan.messages

object ExceptionText {
  /* in case of error, gets the stacktrace */
  def extractWholeExceptionText(ex : Throwable) : String =
    ex.getMessage + " at:\n"+ ex.getStackTrace.mkString("\n") + Option(ex.getCause).map(c => "caused by:\n"+extractWholeExceptionText(c)).getOrElse("")

  /* in case of error, gets the stacktrace */
  def extractMessageText(ex : Throwable) : String =
    ex.getMessage + Option(ex.getCause).map(c => " "+extractMessageText(c)).getOrElse("")

}
