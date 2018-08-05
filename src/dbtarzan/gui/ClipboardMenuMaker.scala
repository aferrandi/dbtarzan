package dbtarzan.gui

import scalafx.scene.control.MenuItem
import scalafx.event.ActionEvent
import dbtarzan.gui.util.JFXUtil
import scalafx.Includes._

/* create a menu to copy text to clipboard */
object ClipboardMenuMaker
{
	/* the text is given by the result of the extractText function */
	def buildClipboardMenu(title : String, extractText : () => String) = new MenuItem {
    	text = title
    	onAction = (ev: ActionEvent) =>  try {
        	JFXUtil.copyTextToClipboard(extractText())
      	} catch {
        	case ex : Exception => println("Copying to the clipboard using the menu "+title+" got "+ex)
      	}
    }
}