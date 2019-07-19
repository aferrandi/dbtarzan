package dbtarzan.gui.foreignkeys

import scalafx.scene.layout. { HBox, Region, Priority }
import scalafx.scene.control.Button
import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.scene.Parent
import scalafx.geometry.{ Insets, Pos }
import dbtarzan.gui.TControlBuilder
import dbtarzan.localization.Localization


class AdditionalButtons(localization: Localization) extends TControlBuilder {
  val buttonNew = new Button {
    text = localization.new_
  }
  
  val buttonCancel = new Button {
    text = localization.cancel
    alignmentInParent = Pos.CENTER_RIGHT
  }

  val buttonSave = new Button {
    text = localization.save
    alignmentInParent = Pos.CENTER_RIGHT
  }

	private val layout = new HBox {
    children = List(buttonNew, new Region() { hgrow = Priority.Always }, buttonSave, buttonCancel )
  	padding = Insets(10)
  	spacing = 10
  }
 
  def onNew(action : () => Unit ): Unit = 
    buttonNew.onAction = (event: ActionEvent)  => action()

  def onSave(action : () => Unit ): Unit =
    buttonSave.onAction = (event: ActionEvent)  => action()

  def onCancel(action : () => Unit ): Unit =
    buttonCancel.onAction = (event: ActionEvent)  => action()
  
  def control : Parent = layout
}
