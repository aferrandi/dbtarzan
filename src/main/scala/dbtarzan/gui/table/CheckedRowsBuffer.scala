package dbtarzan.gui.table

import scala.collection.mutable.ListBuffer
import dbtarzan.db.Row

/* The checked rows in the table. Filled up every time the user chcecks or unchecks a row */
class CheckedRowsBuffer {
	val selected = new ListBuffer[Row]()

	def add(row : Row) : Unit = selected += row

	def remove(row : Row) : Unit = selected -= row

	def rows: List[Row] = selected.toList
}