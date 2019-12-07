package dbtarzan.gui.util

import scalafx.scene.image.Image
import dbtarzan.messages._

object LogIcons {
	val errorIcon = JFXUtil.loadIcon("error.png")
	val warnIcon = JFXUtil.loadIcon("warn.png")
	val infoIcon = JFXUtil.loadIcon("info.png")
	  
	def iconForMessage(msg : TLogMessage) : Image = msg match {
		case e: Error => errorIcon
		case e: Warning => warnIcon  
		case e: Info => infoIcon  
	} 
}