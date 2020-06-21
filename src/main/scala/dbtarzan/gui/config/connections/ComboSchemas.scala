package dbtarzan.gui.config.connections

import scalafx.scene.control.{ComboBox, ListCell}
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import dbtarzan.db.{Schema, Schemas}
import dbtarzan.gui.TControlBuilder
import scalafx.event.ActionEvent
import scalafx.util.StringConverter

/* A combo box from which to select the identfier delimiters that get stored in the configuration file */
class ComboSchemas() extends TControlBuilder with TCombo {
  private val schemas = ObservableBuffer.empty[Option[Schema]]
  private val cmbSchemas = new ComboBox[Option[Schema]] {
    items = schemas
    editable = true
    value = None
    cellFactory = { _ => buildCell() }
    buttonCell = buildCell()
    converter = new StringConverter[Option[Schema]] {
      override def fromString(v: String): Option[Schema] =
        Some(v.trim()).filter(!_.isEmpty).map(t => Schema(t))

      override def toString(t: Option[Schema]): String =
        t.map(_.name).getOrElse("")
    }
  }

  private def buildCell() = new ListCell[Option[Schema]] {
    item.onChange { (value , oldValue, newValue) => {
      val optValue = Option(newValue).flatten
      // the orElse is to avoid problems when removing items
      val valueOrEmpty = optValue.map(value => value.name).orElse(Some(""))
      valueOrEmpty.foreach({ text.value = _ })
    }}}

  def schemasToChooseFrom(newSchemas: Schemas): Unit = {
    schemas.clear()
    schemas.addAll(newSchemas.schemas.map(Some(_)))
  }

  def clearSchemasToChooseFrom() : Unit =
    schemas.clear()

  def show(schema : Option[Schema]) : Unit =
    cmbSchemas.value = schema

  def toSchema() : Option[Schema] = cmbSchemas.getSelectionModel.selectedItem()

  def control : Parent = cmbSchemas

  def onChanged(useSchemas : () => Unit) : Unit = {
    cmbSchemas.onAction = (ev: ActionEvent) => useSchemas()
  }

}

