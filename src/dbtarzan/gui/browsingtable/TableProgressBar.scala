package dbtarzan.gui.browsingtable

import scalafx.scene.control.ProgressBar
import scala.collection.mutable.HashSet
import scalafx.scene.Parent
import dbtarzan.gui.TControlBuilder


object TableProgressBar {
	val STEPS = 3
	val PROGRESSTEP = 1.0 / STEPS 
}

/**
	Progress bar that signals when we got (at least one row of) the query result and the foreign keys
*/
class TableProgressBar(complete: () => Unit) extends TControlBuilder {
	private val bar = new ProgressBar() {
		prefWidth = Double.MaxValue
	}

	private val setReceived = new HashSet[String]()

	private def updateProgressBar() : Unit =  {
		bar.progress() = setReceived.size * TableProgressBar.PROGRESSTEP
		if(setReceived.size >= TableProgressBar.STEPS)
			complete()
	}

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