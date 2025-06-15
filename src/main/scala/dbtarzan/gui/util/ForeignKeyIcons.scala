package dbtarzan.gui.util

import dbtarzan.db.ForeignKeyDirection
import dbtarzan.db.ForeignKeyDirection.{STRAIGHT, TURNED}
import scalafx.scene.image.Image

object ForeignKeyIcons {
	val straightIcon: Image = JFXUtil.loadIcon("foreignKeyStraight.png")
	val turnedUIcon: Image = JFXUtil.loadIcon("foreignKeyTurned.png")
	  
	def iconForDirection(direction : ForeignKeyDirection) : Image = direction match {
		case STRAIGHT => straightIcon
		case TURNED => turnedUIcon
	}
}