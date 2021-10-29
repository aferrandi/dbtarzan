package dbtarzan.gui.util.orderedlist

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.Includes._

class ButtonBuilderUp[T] extends OrderedListButtonBuilder[T] {
  def onChange(value : T, listBuffer : ObservableBuffer[T], comboBuffer: ObservableBuffer[T]) = new Button {
    text = "▲"
    stylesheets += "rowButton.css"
    onAction = {
      (e: ActionEvent) => {
        val index = listBuffer.indexOf(value)
        if(index > 0) {
          listBuffer.remove(index)
          listBuffer.insert(index - 1, value)
        }
      }
    }
  }
}
