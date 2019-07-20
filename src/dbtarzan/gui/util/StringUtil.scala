package dbtarzan.gui.util

object StringUtil {
    def shortenIfTooLong(text: String, maxLength : Int) : String =
        if(text.length <= maxLength)
            text
        else if(maxLength > 3)
            text.take(maxLength - 3)+"..."
        else
            text.take(maxLength)

    def textIf(condition : Boolean, builder: () => String) : String = 
      if(condition) 
        builder() 
      else 
        ""            
}