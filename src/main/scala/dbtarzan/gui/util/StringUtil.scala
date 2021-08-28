package dbtarzan.gui.util

object StringUtil {
    def shortenIfTooLong(text: String, maxLength : Int) : String =
        if(text.length <= maxLength)
            text
        else if(maxLength > 3)
            text.take(maxLength - 3)+"..."
        else
            text.take(maxLength)

    /* to include a text in a string depening by a condition */
    def textIf(condition : Boolean, builder: () => String) : String = 
      if(condition) 
        builder() 
      else 
        ""

  def isAllDigits(x: String): Boolean = x forall Character.isDigit

  def noneToEmpty(optS : Option[String]) : String =
    optS.getOrElse("")
  def emptyToNone(s : String) : Option[String] =
    Option(s).filter(_.trim.nonEmpty)
}