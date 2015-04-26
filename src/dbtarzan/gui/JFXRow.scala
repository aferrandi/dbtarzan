package dbtarzan.gui

import scalafx.beans.property.{StringProperty, ObjectProperty, BooleanProperty}
import dbtarzan.db.{Row, Rows, Field}
import scalafx.Includes._

/**
	One row of a table. The first column is for the check box, the others come from the database
*/
case class JFXRow(checked: BooleanProperty, values : List[StringProperty], row : Row)


/* needed to have the check box working */
class JFXRowFromRow(checked : CheckedRows) {
	def apply(rows : Rows, columnNames: List[Field]) : List[JFXRow] = rows.rows.map(row => {
		val jfxRow = JFXRow(checkedProperty(), values(row, columnNames), row)
		jfxRow.checked.onChange((_, _, newValue) => fromCheckBoxToChecked(newValue, row))
		jfxRow
	})

	/* if the check box gets checked or unchecked the related row gets added or removed from the list of checked rows */
	private def fromCheckBoxToChecked(newValue : Boolean, row : Row) = 			
		if(newValue) 
			checked.add(row)
		else
			checked.remove(row)
	/* creates the fields of a row */
	private def values(row : Row, columnNames: List[Field]) = {
		if(row.values.size != columnNames.size)
			throw new Exception("column sizes "+columnNames+" <> row cells size "+row.values)
		row.values.zipWithIndex.map({ case (value, i) => valueToProperty(columnNames(i), value)})
	}
	/* creates the cell in the row */
	private def valueToProperty(field : Field, value : String) = 
		new StringProperty(this, field.name, value)

	/* the checkbox field property */
	private def checkedProperty() =
		new BooleanProperty(this, "selected")
}