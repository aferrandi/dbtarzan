package dbtarzan.gui.util


class OnChangeSafe {
	var changEvents = true

	def noChangeEventDuring(action : () => Unit) : Unit = {
		changEvents = false
		action()
		changEvents = true
	}

	def onChange(action : () => Unit) : Unit =
		if(changEvents)
			action()

}