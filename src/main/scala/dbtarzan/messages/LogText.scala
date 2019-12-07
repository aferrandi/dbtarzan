package dbtarzan.messages

/* converts the log messages to prinatable texts */
object LogText {
	/* short description */
	def extractLogMessage(msg : TLogMessage) : String = msg match {
		case Error(_, text, Some(ex)) => text + ":" + extractMessageText(ex)
		case Error(_, text, None) => text
		case Warning(_, text) => text  
		case Info(_, text) => text  
	}

	/* to show in the log view if it is an error, warning or info message */
	def extractLogPrefix(msg : TLogMessage) : String =msg match {
		case e: Error => "E"
		case e: Warning => "W"  
		case e: Info => "I"  
	} 
	
	/* long descrption, to show when the user examines the message */
	def extractWholeLogText(msg : TLogMessage) : String =  msg match { 
		case Error(_, text, Some(ex)) => text + ":" + extractWholeExceptionText(ex)
		case Error(_, text, None) => text
		case Warning(_, text) => text
		case Info(_, text) => text
	}	 

	/* in case of error, gets the stacktrace */
	private def extractWholeExceptionText(ex : Throwable) : String =
    ex.getMessage + " at:\n"+ ex.getStackTrace.mkString("\n") + Option(ex.getCause).map(c => "caused by:\n"+extractWholeExceptionText(c)).getOrElse("")

  /* in case of error, gets the stacktrace */
  private def extractMessageText(ex : Throwable) : String =
    ex.getMessage + Option(ex.getCause).map(c => " "+extractMessageText(c)).getOrElse("")

}