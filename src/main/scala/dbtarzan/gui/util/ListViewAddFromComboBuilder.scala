package dbtarzan.gui.util

import dbtarzan.gui.util.listviewbuttons.{ButtonDown, ButtonUp}
import scalafx.scene.Parent

object ListViewAddFromComboBuilder {
  def buildOrdered[T](addButtonLabel: String,
                      cellBuilder: Option[T] => Parent,
                      comboStrategy: TComboStrategy[T]) = new ListViewAddFromCombo[T](
    addButtonLabel,
    cellBuilder,
    comboStrategy,
    List(new ButtonDown[T](), new ButtonUp[T])
  )

  def buildUnordered[T](addButtonLabel: String,
                      cellBuilder: Option[T] => Parent,
                      comboStrategy: TComboStrategy[T]) = new ListViewAddFromCombo[T](
    addButtonLabel,
    cellBuilder,
    comboStrategy,
    List.empty
  )
}
