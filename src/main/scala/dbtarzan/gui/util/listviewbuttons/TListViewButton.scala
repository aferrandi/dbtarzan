package dbtarzan.gui.util.listviewbuttons

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Button

trait TListViewButton[T] {
  def buildFor(value: T, listBuffer: ObservableBuffer[T]): Button
}
