package dbtarzan.gui

import scala.collection.mutable.ListBuffer
import dbtarzan.db.Row

/**
	The checked boxes in the table
*/
class CheckedRows {
	val selected = new ListBuffer[Row]()

	def add(row : Row) : Unit = selected += row

	def remove(row : Row) : Unit = selected -= row

	def rows = selected.toList
}