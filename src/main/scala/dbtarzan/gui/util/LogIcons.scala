package dbtarzan.gui.util

import scalafx.scene.image.Image
import dbtarzan.messages._

object LogIcons {
	val errorIcon: Image = JFXUtil.loadIcon("error.png")
	val warnIcon: Image = JFXUtil.loadIcon("warn.png")
	val infoIcon: Image = JFXUtil.loadIcon("info.png")
	  
	def iconForMessage(msg : TLogMessage) : Image = msg match {
		case _: Error => errorIcon
		case _: Warning => warnIcon
		case _: Info => infoIcon
	} 
}