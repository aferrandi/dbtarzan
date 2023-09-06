package dbtarzan.gui.rowdetails

import scalafx.scene.Node

trait TRowDetailsCell {
  def showText(value: String | Int | Double): Unit
  def control: Node
}
