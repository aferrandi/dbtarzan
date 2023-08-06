package dbtarzan.gui.util

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.geometry.Insets
import scalafx.scene.Parent
import scalafx.scene.control.TextField

class FilterText(action : String => Unit, localization : Localization)  extends TControlBuilder {
  private val filterText = new TextField() {
    promptText = localization.filter
    margin = Insets(0,0,3,0)
    text.onChange { (_ , _, newValue) => {
      val optValue = Option(newValue)
      optValue.foreach({ action(_)  })
    }}
  }

  def control : Parent = filterText
}
