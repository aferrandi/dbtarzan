package dbtarzan.gui.util.listviewbuttons


import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Button

class ButtonDown[T] extends TListViewButton[T] {
  def buildFor(value: T, listBuffer: ObservableBuffer[T]): Button = ButtonBuilder.buildButton(value, "▼", (value: T) =>
    moveDown(value, listBuffer)
  )

  private def moveDown(value: T, listBuffer: ObservableBuffer[T]): Unit = {
    val index = listBuffer.indexOf(value)
    if (index < listBuffer.length - 1) {
      listBuffer.remove(index)
      listBuffer.insert(index + 1, value)
    }
  }
}
