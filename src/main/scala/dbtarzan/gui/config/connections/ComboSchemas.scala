package dbtarzan.gui.config.connections

import dbtarzan.db.SchemaName
import dbtarzan.gui.interfaces.TControlBuilder
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Parent
import scalafx.scene.control.{ComboBox, ListCell}
import scalafx.util.StringConverter

import scala.collection.immutable

/* A combo box from which to select the identfier delimiters that get stored in the configuration file */
class ComboSchemas() extends TControlBuilder with TCombo {
  private val schemas = ObservableBuffer.empty[Option[SchemaName]]
  private val cmbSchemas = new ComboBox[Option[SchemaName]] {
    items = schemas
    editable = true
    value = None
    cellFactory = { _ => buildCell() }
    buttonCell = buildCell()
    converter = new StringConverter[Option[SchemaName]] {
      override def fromString(v: String): Option[SchemaName] =
        items.value.find(SchemaName => SchemaName.get.schema == v.trim()).get

      override def toString(t: Option[SchemaName]): String =
        t.map(_.schema).getOrElse("")
    }
  }

  private def buildCell() = new ListCell[Option[SchemaName]] {
    item.onChange { (value , oldValue, newValue) => {
      val optValue = Option(newValue).flatten
      // the orElse is to avoid problems when removing items
      val valueOrEmpty = optValue.map(value => value.schema).orElse(Some(""))
      valueOrEmpty.foreach({ text.value = _ })
    }}}

  def schemasToChooseFrom(newSchemas: List[SchemaName]): Unit = {
    schemas.clear()
    val schemasToAdd: immutable.List[Some[SchemaName]] = newSchemas.map(Some(_))
    schemas.addAll(schemasToAdd)
  }

  def clearSchemasToChooseFrom() : Unit =
    schemas.clear()

  def show(schema : Option[SchemaName]) : Unit =
    cmbSchemas.value = schema

  def chosenSchema(): Option[SchemaName] = cmbSchemas.getSelectionModel.selectedItem()

  def control : Parent = cmbSchemas

  def onChanged(useSchemas : () => Unit) : Unit = {
    cmbSchemas.onAction = (ev: ActionEvent) => useSchemas()
  }
}

