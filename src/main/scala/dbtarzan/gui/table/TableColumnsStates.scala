package dbtarzan.gui.table

object TableColumnsStates {
  val PRIMARYKEY_STATE: Int = 1
  val FOREIGNKEY_STATE: Int = 2
  val BOTHKEYS_STATE: Int = PRIMARYKEY_STATE + FOREIGNKEY_STATE
}
