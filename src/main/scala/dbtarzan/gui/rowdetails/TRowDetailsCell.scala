package dbtarzan.gui.rowdetails

import dbtarzan.types.Binaries.Binary
import scalafx.scene.Node

trait TRowDetailsCell {
  def showText(value: String | Int | Double | Binary): Unit
  def control: Node
}
