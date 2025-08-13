package dbtarzan.gui.config.connections

import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
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
    println(f"show $leftFunction")
    cmbDeLeftFunction.value = leftFunction
    cmbDeLeftFunction.editor.value.text = leftFunction
  }

  def retrieveLeftFunction() : Option[String] = {
    val text = cmbDeLeftFunction.value.value
    if(!text.isBlank)
      Some(text)
    else
      None
  }

  def control : Parent = cmbDeLeftFunction

  def onChanged(useLeftFunction : () => Unit) : Unit = {
    cmbDeLeftFunction.editor.value.textProperty().onChange(
      (_, _, _) => { cmbDeLeftFunction.value.value = cmbDeLeftFunction.editor.value.text(); useLeftFunction() }
    )
  }
}

