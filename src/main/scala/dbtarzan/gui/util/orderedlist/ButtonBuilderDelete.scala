package dbtarzan.gui.util.orderedlist

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.Includes._

class ButtonBuilderDelete[T] extends OrderedListButtonBuilder[T] {
  def onChange(value : T, listBuffer : ObservableBuffer[T], comboBuffer: ObservableBuffer[T]) =new Button {
    text = "X"
    stylesheets += "rowButton.css"
    onAction = {
      (e: ActionEvent) => {
        listBuffer -= value
        comboBuffer += value
      }
    }
  }
}
