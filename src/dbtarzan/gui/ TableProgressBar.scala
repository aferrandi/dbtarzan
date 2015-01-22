package dbtarzan.gui

import scalafx.scene.control.ProgressBar
import scala.collection.mutable.HashSet
import scalafx.scene.layout.Priority

/**
	Progress bar that signals when we got (at least one row of) the query result and the foreign keys
*/
class TableProgressBar {
	val bar = new ProgressBar() {
		prefWidth = Double.MaxValue
	}
	val setReceived = new HashSet[String]()

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
}