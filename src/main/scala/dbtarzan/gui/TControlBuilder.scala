package dbtarzan.gui

import scalafx.scene.Parent

/* a class that builds a control and exposes it via the "control" method */
trait TControlBuilder {
	def control : Parent
}
