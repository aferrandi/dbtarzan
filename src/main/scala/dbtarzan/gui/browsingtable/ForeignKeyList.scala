package dbtarzan.gui.browsingtable

import dbtarzan.db.{ForeignKey, ForeignKeys}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.TLogger
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{ListView, Tooltip}

/* if the table has 2 or more foreign keys to the same table, we want to give more information to the user, so that he can understand which one to use */ 
case class ForeignKeyWithSharingCheck(key: ForeignKey, sharesToTable : Boolean)

/**	foreign keys list */
class ForeignKeyList(log: TLogger) extends TControlBuilder {
  private val buffer = ObservableBuffer.empty[ForeignKeyWithSharingCheck]
  private val list = new ListView[ForeignKeyWithSharingCheck](buffer) {
      cellFactory =  (cell, value) => {
        cell.tooltip.value = Tooltip(ForeignKeyText.buildTooltip(value.key))
        cell.text.value = ForeignKeyText.buildText(value)
      }
    }

  /** need to show only the "to table" as cell text. And a tooltip for each cell	*/
  def addForeignKeys(newForeignKeys : ForeignKeys) : Unit = {
    setForeignKeys(newForeignKeys)
  }

  private def setForeignKeys(newForeignKeys: ForeignKeys): Unit = {
    def moreThanOneItem(l: List[?]) = l.length > 1
    log.debug("newForeignKeys " + newForeignKeys)
    val allForeignKeys = buffer.toList.map(_.key) ++ newForeignKeys.keys
    val groupedByToTableInsensitive = allForeignKeys.groupBy(_.to.table.tableName.toUpperCase()).values
    val withSharingCheck = groupedByToTableInsensitive.flatMap(ks => ks.map(ForeignKeyWithSharingCheck(_, moreThanOneItem(ks))))
    JFXUtil.bufferSet(buffer, withSharingCheck)
  }

  def setForeignKeysByPattern(newForeignKeys : ForeignKeys) : Unit = {
    buffer.clear()
    setForeignKeys(newForeignKeys)
  }

  /* foreign key double-clicked. handled by BrowsingTable that has knowledge of tables too */
  def onForeignKeySelected(useKey : (ForeignKey, Boolean)  => Unit) : Unit =
     JFXUtil.onAction(list, { (selectedKey : ForeignKeyWithSharingCheck, ctrlDown) =>
        log.debug(s"Selected $selectedKey")
        Option(selectedKey).foreach(k => useKey(k.key, ctrlDown))
      })
  def control : Parent = list
 }

