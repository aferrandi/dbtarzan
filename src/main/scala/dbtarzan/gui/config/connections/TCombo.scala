package dbtarzan.gui.config.connections

trait TCombo {
  def onChanged(useData : () => Unit) : Unit
}
