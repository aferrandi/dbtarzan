package dbtarzan.gui

import scalafx.beans.property.{StringProperty, ObjectProperty, BooleanProperty}
import dbtarzan.db.{Row, Rows, Field}
import scalafx.Includes._

/**
	One row of a table, active
*/
case class JFXRow(selected: BooleanProperty, values : List[StringProperty], row : Row)


class JFXRowFromRow(selected : SelectedRows) {
	def apply(rows : Rows, columnNames: List[Field]) : List[JFXRow] = rows.rows.map(row => {
		val jfxRow = JFXRow(selectedProperty(), values(row, columnNames), row)
		jfxRow.selected.onChange((_, _, newValue) => fromCheckBoxToSelected(newValue, row))
		jfxRow
	})

	private def fromCheckBoxToSelected(newValue : Boolean, row : Row) = 			
		if(newValue) 
			selected.add(row)
		else
			selected.remove(row)

	private def values(row : Row, columnNames: List[Field]) = {
		if(row.values.size != columnNames.size)
			throw new Exception("column sizes "+columnNames+" <> row cells size "+row.values)
		row.values.zipWithIndex.map({ case (value, i) => valueToProperty(columnNames(i), value)})
	}

	private def valueToProperty(field : Field, value : String) = 
		new StringProperty(this, field.name, value)

	private def selectedProperty() =
		new BooleanProperty(this, "selected")
}