package dbtarzan.gui.config.global

import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.scene.layout.HBox
import scalafx.scene.control.Button
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }
import dbtarzan.localization.Localization


class GlobalButtons(
  localization: Localization
) extends TControlBuilder {
  private val buttonCancel = new Button {
    text = localization.cancel
    alignmentInParent = Pos.CenterRight
  }

  private val buttonSave = new Button {
    text = localization.save
    alignmentInParent = Pos.CenterRight
  }

  private val layout = new HBox {
    children = List(buttonSave, buttonCancel )
    padding = Insets(10)
    spacing = 10
  }
 
  def onSave(action : () => Unit ): Unit =
    buttonSave.onAction = (_: ActionEvent)  => action()

  def onCancel(action : () => Unit ): Unit =
    buttonCancel.onAction = (_: ActionEvent)  => action()
  
  def control : Parent = layout
}
