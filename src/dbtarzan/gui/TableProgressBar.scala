package dbtarzan.gui

import scalafx.scene.control.ProgressBar
import scala.collection.mutable.HashSet
import scalafx.scene.layout.Priority
import scalafx.scene.Parent

/**
	Progress bar that signals when we got (at least one row of) the query result and the foreign keys
*/
class TableProgressBar extends TControlBuilder {
	private val bar = new ProgressBar() {
		prefWidth = Double.MaxValue
	}
	private val setReceived = new HashSet[String]()

	private def updateProgressBar() : Unit = 
		bar.progress() = setReceived.size * .5

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