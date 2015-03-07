package dbtarzan.messages

object ErrorText {
	def extractErrorMessage(msg : TTextMessage) : String = msg match {
		case Error(ex) => ex.getMessage()
		case Warning(text) => text  
	}
	
	def extractWholeErrorText(msg : TTextMessage) : String =  msg match { 
		case Error(ex) => ex.getMessage()+ " at:\n"+ extractStackTrace(ex)
		case Warning(text) => text
	}	 

	private def extractStackTrace(ex : Exception) : String =
		ex.getStackTrace().mkString("\n")   
}