package dbtarzan.messages

/* converts the log messages to prinatable texts */
object LogText {
	/* short description */
	def extractLogMessage(msg : TLogMessage) : String = msg match {
		case Error(_, text, Some(ex)) => text + ":" + ex.getMessage()
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
		case Error(_, text, Some(ex)) => text + ":" + ex.getMessage()+ " at:\n"+ extractStackTrace(ex)
		case Error(_, text, None) => text
		case Warning(_, text) => text
		case Info(_, text) => text
	}	 

	/* in case of error, gets the stacktrace */
	private def extractStackTrace(ex : Exception) : String =
		ex.getStackTrace().mkString("\n")   
}