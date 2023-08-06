package dbtarzan.gui.config.connections

import scalafx.scene.control.{ComboBox, ListCell}
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import dbtarzan.db.{IdentifierDelimiters, IdentifierDelimitersValues}
import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.event.ActionEvent

/* A combo box from which to select the identfier delimiters that get stored in the configuration file */
class ComboDelimiters() extends TControlBuilder with TCombo {
  private val possibleDelimiters : ObservableBuffer[Option[IdentifierDelimiters]] = ObservableBuffer.from(List(
    None,
    Some(IdentifierDelimitersValues.squareBrackets),
    Some(IdentifierDelimitersValues.doubleQuotes)
  ))
  private val cmbDelimiters = new ComboBox[Option[IdentifierDelimiters]] {
    items = possibleDelimiters
    editable = false
    value = None
    cellFactory = (cell, value) => {
      // the orElse is to avoid problems when removing items
      val valueOrEmpty = value.map(v => "" + v.start + " " + v.end).orElse(Some(""))
      valueOrEmpty.foreach({
        cell.text.value = _
      })
    }
    buttonCell = buildCell()
  }
 
  private def buildCell() = new ListCell[Option[IdentifierDelimiters]] {
    item.onChange { (_ , _, newValue) => {
        val optValue = Option(newValue).flatten
        // the orElse is to avoid problems when removing items
        val valueOrEmpty = optValue.map(value => "" + value.start+" "+value.end).orElse(Some(""))
        valueOrEmpty.foreach({ text.value = _ })
      }}}


  def show(delimiters : Option[IdentifierDelimiters]) : Unit = {
    cmbDelimiters.value = delimiters
  }

  def toDelimiters() : Option[IdentifierDelimiters] = cmbDelimiters.getSelectionModel.selectedItem()

  def control : Parent = cmbDelimiters

  def onChanged(useDelimiters : () => Unit) : Unit = {  
    cmbDelimiters.onAction = (_: ActionEvent) => useDelimiters()
  }
}

