package dbtarzan.db.actor

object ExceptionToHandler {
  /* handles the exceptions sending the exception messages to the GUI */
  def handleErr[R](errHandler: Exception => Unit, operation: => R): Unit =
    try {
      operation
    } catch {
      case e: Exception => errHandler(e)
    }
}
