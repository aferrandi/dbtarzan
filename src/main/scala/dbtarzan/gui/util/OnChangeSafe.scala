package dbtarzan.gui.util


/* onChange does not run the action if noChangeEventDuring is running. Works with GUI events */ 
class OnChangeSafe {
	var changeEvents = true

	def noChangeEventDuring(action : () => Unit) : Unit = {
		changeEvents = false
		action()
		changeEvents = true
	}

	def onChange(action : () => Unit) : Unit =
		if(changeEvents)
			action()
}