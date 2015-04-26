package dbtarzan.gui

import scalafx.scene.Node

/* a class that builds a control and exposes it via the "control" method */
trait TControlBuilder {
	def control : Node
}
