package dbtarzan.messages

trait TLogger {
  def debug(text : String) : Unit

  def info(text : String) : Unit

  def warning(text : String) : Unit

  def error(text : String, ex : Exception) : Unit

  def error(text : String) : Unit
}
