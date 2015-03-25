package dbtarzan.messages

object ErrorText {
	def extractErrorMessage(msg : TTextMessage) : String = msg match {
		case Error(text, ex) => text + ":" + ex.getMessage()
		case Warning(text) => text  
		case Info(text) => text  
	}
	
	def extractWholeErrorText(msg : TTextMessage) : String =  msg match { 
		case Error(text, ex) => text + ":" + ex.getMessage()+ " at:\n"+ extractStackTrace(ex)
		case Warning(text) => text
		case Info(text) => text
	}	 

	private def extractStackTrace(ex : Exception) : String =
		ex.getStackTrace().mkString("\n")   
}