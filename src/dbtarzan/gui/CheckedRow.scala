package dbtarzan.gui

import scalafx.beans.property.{StringProperty, BooleanProperty}
import dbtarzan.db.{Row, Rows, Field, FieldType}
import scalafx.Includes._
import scalafx.scene.control.MultipleSelectionModel
/**
	One row of a table. The first column is for the check box, the others come from the database
*/
case class CheckedRow(checked: BooleanProperty, values : List[StringProperty], row : Row)


/* needed to have the check box working */
class CheckedRowFromRow(checked : CheckedRowsBuffer, selectionModel: MultipleSelectionModel[CheckedRow]) {
	def apply(rows : Rows, columnNames: List[Field]) : List[CheckedRow] = 
		rows.rows.map(row => buildCheckedRow(row, columnNames))
		
	/* builds the row representation used as row of the table */
	private def buildCheckedRow(row : Row, columnNames: List[Field]) : CheckedRow = {
		val checkedRow = CheckedRow(checkedProperty(), values(row, columnNames), row)
		onCheckedRowCheck(checkedRow)
		checkedRow
	}
	/* when the checkbox on the row get checked, also the selected rows get checked and the list of checked rows gets filled up with the checked rows */
	private def onCheckedRowCheck(checkedRow : CheckedRow) : Unit =	{
		def checkedRowCheckAction(newValue : Boolean, row : Row) : Unit = {
			checkSelectedRows(newValue)
			fromCheckBoxToChecked(newValue, row)
		}
		checkedRow.checked.onChange((_, _, newValue) => checkedRowCheckAction(newValue, checkedRow.row))
	}

	/* if someone checks one row but has other rows selected we want also the selected rows to be checked */
	private def checkSelectedRows(newValue : Boolean) : Unit = {
		selectionModel.selectedItems.foreach(checkedRow => checkedRow.checked() = newValue)
	}

	/* if the check box gets checked or unchecked the related row gets added or removed from the list of checked rows */
	private def fromCheckBoxToChecked(newValue : Boolean, row : Row) : Unit = 			
		if(newValue) 
			checked.add(row)
		else
			checked.remove(row)
	
	/* creates the fields of a row */
	private def values(row : Row, columnNames: List[Field]) : List[StringProperty] = {
		if(row.values.size != columnNames.size)
			throw new Exception("column sizes "+columnNames+" <> row cells size "+row.values)
		row.values.zipWithIndex.map({ case (value, i) => valueToProperty(columnNames(i), value)})
	}
	/* creates the cell in the row */
	private def valueToProperty(field : Field, value : String) =
		new StringProperty(this, field.name, toDisplayOnlyOneLine(field, value))
	
	/* text with multiple lines make tables unusable. Keep one line. The other lines are displayed in the RowDetailsView */
	private def toDisplayOnlyOneLine(field : Field, value : String) : String = field.fieldType match {
		case FieldType.STRING => Option(value).map(s => truncateToFirstLine(s)).getOrElse("")
		case _ => value
	}

	/* if there are multiple lines returns only the first line followed by "...". */
	private def truncateToFirstLine(s : String) : String = 
		if(s.contains('\n'))
			s.takeWhile(c => c != '\n' && c != '\r')+"..."
		else
			s 

	/* the checkbox field property */
	private def checkedProperty() =
		new BooleanProperty(this, "selected")
}