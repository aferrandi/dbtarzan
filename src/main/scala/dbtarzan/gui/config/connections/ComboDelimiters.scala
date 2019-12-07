package dbtarzan.gui.config.connections

import scalafx.scene.control.{ ComboBox, ListCell }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import dbtarzan.db.IdentifierDelimiters
import dbtarzan.gui.TControlBuilder
import scalafx.event.ActionEvent

/**
  A combo box from which to select the identfier delimiters that get stored in the configuration file
*/
class ComboDelimiters() extends TControlBuilder {
 val possibleDelimiters = ObservableBuffer(List(
    None,
    Some(IdentifierDelimiters('[', ']')),
    Some(IdentifierDelimiters('"', '"'))
  ))
  val cmbDelimiters = new ComboBox[Option[IdentifierDelimiters]] {
    items = possibleDelimiters
    editable = false
    value = None
    cellFactory = { _ => buildCell() }
    buttonCell = buildCell() 
  }
 
  private def buildCell() = new ListCell[Option[IdentifierDelimiters]] {
    item.onChange { (value , oldValue, newValue) => {
        val optValue = Option(newValue).flatten
        // the orElse is to avoid problems when removing items
        val valueOrEmpty = optValue.map(value => value.start+" "+value.end).orElse(Some(""))
        valueOrEmpty.foreach({ text.value = _ })
      }}}


  def show(delimiters : Option[IdentifierDelimiters]) : Unit = {
    cmbDelimiters.value = delimiters; 
  }

  def toDelimiters() : Option[IdentifierDelimiters] = cmbDelimiters.getSelectionModel().selectedItem()

  def control : Parent = cmbDelimiters

  def onChanged(useDelimiters : () => Unit) : Unit = {  
    cmbDelimiters.onAction = (ev: ActionEvent) => useDelimiters()
  }
}

