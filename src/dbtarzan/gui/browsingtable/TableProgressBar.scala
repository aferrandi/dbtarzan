package dbtarzan.gui.browsingtable

import scalafx.scene.control.ProgressBar
import scala.collection.mutable.HashSet
import scalafx.scene.Parent
import dbtarzan.gui.TControlBuilder

/**
	Progress bar that signals when we got (at least one row of) the query result and the foreign keys
*/
class TableProgressBar extends TControlBuilder {
	private val bar = new ProgressBar() {
		prefWidth = Double.MaxValue
	}
	private val setReceived = new HashSet[String]()

	private def updateProgressBar() : Unit = 
		bar.progress() = setReceived.size * .333

	def receivedPrimaryKeys() : Unit = {
		setReceived += "PRIMARYKEYS"
		updateProgressBar()
	}

	def receivedForeignKeys() : Unit = {
		setReceived += "FOREIGNKEYS"
		updateProgressBar()
	}

	def receivedRows() : Unit = {
		setReceived += "ROWS"
		updateProgressBar()
	}

	def control : Parent = bar
}