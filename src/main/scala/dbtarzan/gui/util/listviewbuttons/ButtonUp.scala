package dbtarzan.gui.util.listviewbuttons

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Button

class ButtonUp[T] extends TListViewButton[T] {
  def buildFor(value: T, listBuffer: ObservableBuffer[T]): Button = ButtonBuilder.buildButton(value, "▲", (value: T) =>
    moveUp(value, listBuffer)
  )

  private def moveUp(value: T, listBuffer: ObservableBuffer[T]): Unit = {
    val index = listBuffer.indexOf(value)
    if (index > 0) {
      listBuffer.remove(index)
      listBuffer.insert(index - 1, value)
    }
  }
}
