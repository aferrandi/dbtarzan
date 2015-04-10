package dbtarzan.gui

import scalafx.scene.control.MenuItem
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil
import scalafx.Includes._

/* create a menu to copy text to clipboard */
object ClipboardMenuMaker
{
	/* the text is given by the result of the extractText function */
	def buildClipboardMenu(whatToCopy : String, extractText : () => String) = new MenuItem {
    	text = "Copy "+whatToCopy+" To Clipboard"
    	onAction = (ev: ActionEvent) =>  try {
        	JFXUtil.copyTextToClipboard(extractText())
      	} catch {
        	case ex : Exception => println("Copying "+whatToCopy+" to the clipboard got "+ex)
      	}
    }
}