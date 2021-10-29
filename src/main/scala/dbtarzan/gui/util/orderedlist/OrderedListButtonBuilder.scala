package dbtarzan.gui.util.orderedlist

import scalafx.collections.ObservableBuffer
import scalafx.scene.control.Button

trait OrderedListButtonBuilder[T] {
  def onChange(value : T, listBuffer : ObservableBuffer[T], comboBuffer: ObservableBuffer[T]) : Button
}
