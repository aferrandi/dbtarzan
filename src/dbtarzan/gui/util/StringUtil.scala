package dbtarzan.gui.util

object StringUtil {
    def shortenIfTooLong(text: String, maxLength : Int) : String =
        if(text.length <= maxLength)
            text
        else
            text.take(maxLength)+"..."
}