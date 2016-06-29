package dbtarzan.gui.util

import scalafx.scene.image.Image
import dbtarzan.messages._

class LogIcons 
{
	val errorIcon = loadIcon("error.png")
	val warnIcon = loadIcon("warn.png")
	val infoIcon = loadIcon("info.png")

	private def loadIcon(fileName: String) : Image = 
		// println(this.getClass().getResource("").getPath())
	    new Image(getClass().getResourceAsStream(fileName))
	
	  
	def iconForMessage(msg : TLogMessage) : Image = msg match {
		case e: Error => errorIcon
		case e: Warning => warnIcon  
		case e: Info => infoIcon  
	} 
}