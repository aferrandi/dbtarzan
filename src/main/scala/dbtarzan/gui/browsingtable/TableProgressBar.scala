package dbtarzan.gui.browsingtable

import scalafx.scene.control.ProgressBar

import scala.collection.mutable.HashSet
import scalafx.scene.Parent
import dbtarzan.gui.TControlBuilder
import scalafx.scene.layout.StackPane
import scalafx.scene.text.Text


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

  private val text = new Text() {
  }

  private val pane = new StackPane {
    children = List(bar, text)
  }

  private val setReceived = new HashSet[String]()

	private def updateProgressBar() : Unit =  {
		bar.progress() = setReceived.size * TableProgressBar.PROGRESSTEP
    text.text = setReceived.mkString(" + ")
		if(setReceived.size >= TableProgressBar.STEPS)
			complete()
	}

	def receivedPrimaryKeys() : Unit = {
		setReceived += "Primary keys"
		updateProgressBar()
	}

	def receivedForeignKeys() : Unit = {
		setReceived += "Foreign keys"
		updateProgressBar()
	}

	def receivedRows() : Unit = {
		setReceived += "Rows"
		updateProgressBar()
	}

	def control : Parent = pane
}