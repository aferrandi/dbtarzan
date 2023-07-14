package dbtarzan.gui.util.listviewbuttons

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.Includes._

object ButtonBuilder {
  def buildButton[T](value: T, icon: String, onClick: T => Unit): Button = {
    new Button {
      text = icon
      stylesheets += "rowButton.css"
      onAction = {
        (e: ActionEvent) => onClick(value)
      }
    }
  }
}
