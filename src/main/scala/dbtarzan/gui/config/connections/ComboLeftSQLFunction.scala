package dbtarzan.gui.config.connections

import dbtarzan.db.{IdentifierDelimiters, IdentifierDelimitersValues}
import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.{ComboBox, ListCell}


/* A combo box from which to select the identfier delimiters that get stored in the configuration file */
class ComboLeftSQLFunction() extends TControlBuilder with TCombo {
  private val leftFunctions : ObservableBuffer[String] = ObservableBuffer.from(List(
    "LEFT($column, $max)",
    "SUBSTR($column, 1, $max)",
    "SUBSTRING($column FROM 1 FOR $max)"
  ))
  private val cmbDeLeftFunction = new ComboBox[String] {
    items = leftFunctions
    editable = true
    value = ""
    cellFactory = (cell, value) => {
      cell.text.value = value
    }
    buttonCell = buildCell()
  }
 
  private def buildCell() = new ListCell[String] {
    item.onChange { (_ , _, newValue) => {
        text.value = newValue
      }}}


  def show(leftFunction : String) : Unit = {
    cmbDeLeftFunction.value = leftFunction
  }

  def retrieveLeftFunction() : Option[String] = {
    val text = cmbDeLeftFunction.getEditor.getText()
    if(!text.isBlank)
      Some(text)
    else
      None
  }

  def control : Parent = cmbDeLeftFunction

  def onChanged(useDelimiters : () => Unit) : Unit = {
    cmbDeLeftFunction.onAction = (_: ActionEvent) => useDelimiters()
  }
}

