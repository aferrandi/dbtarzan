package dbtarzan.gui.table

import dbtarzan.db.Field
import scalafx.beans.value.ObservableValue
import scalafx.scene.control.TableColumn
import scalafx.scene.control.cell.CheckBoxTableCell
import scalafx.beans.property.StringProperty
import scalafx.Includes._
object TableColumnsBuild {
  def buildStringColumn(field: Field, index: Int): TableColumn[CheckedRow, String] = new TableColumn[CheckedRow, String]() {
    text = field.name
    cellValueFactory = {
      _.value.values(index).asInstanceOf[StringProperty]
    } // when showing a row, shows the value for the column field
    prefWidth = 180
  }.delegate

  def buildBinaryColumn(field: Field, index: Int): TableColumn[CheckedRow, String] = new TableColumn[CheckedRow, String]() {
    text = field.name
    cellValueFactory = {
      _.value.values(index).asInstanceOf[StringProperty]
    } // when showing a row, shows the value for the column field
    prefWidth = 180
  }.delegate

  def buildOtherColumn(field: Field, index: Int): TableColumn[CheckedRow, String] = new TableColumn[CheckedRow, String]() {
    text = field.name
    cellValueFactory = {
      _.value.values(index).asInstanceOf[StringProperty]
    } // when showing a row, shows the value for the column field
    prefWidth = 180
  }.delegate

  def buildIntColumn(field: Field, index: Int): TableColumn[CheckedRow, Int] = new TableColumn[CheckedRow, Int]() {
    text = field.name
    comparator = Ordering.Int
    // style = "-fx-alignment: CENTER-RIGHT;"
    cellValueFactory = {
      _.value.values(index).asInstanceOf[ObservableValue[Int, Int]]
    } // when showing a row, shows the value for the column field
    prefWidth = 180
  }.delegate

  def buildFloatColumn(field: Field, index: Int): TableColumn[CheckedRow, Double] = new TableColumn[CheckedRow, Double]() {
    text = field.name
    comparator = Ordering.Double.TotalOrdering
    cellValueFactory = {
      _.value.values(index).asInstanceOf[ObservableValue[Double, Double]]
    } // when showing a row, shows the value for the column field
    prefWidth = 180
  }.delegate

  /* the ckeck box column is special */
  def buildCheckColumn(): TableColumn[CheckedRow, java.lang.Boolean] = {
    val checkColumn = new TableColumn[CheckedRow, java.lang.Boolean] {
      text = ""
      cellValueFactory = {
        _.value.checked.delegate
      }
      prefWidth = 40
      editable = true
    }
    checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn))
    checkColumn
  }
}
