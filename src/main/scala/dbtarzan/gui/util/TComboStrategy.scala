package dbtarzan.gui.util

import scalafx.collections.ObservableBuffer

trait TComboStrategy[T] {
  def removeFromCombo(comboBuffer: ObservableBuffer[T], item : T): Unit
  def addToCombo(comboBuffer: ObservableBuffer[T], item : T): Unit
}
