package dbtarzan.gui

import dbtarzan.db.DatabaseId
import dbtarzan.gui.interfaces.{TControlBuilder, TDatabaseList}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.{DatabaseIds, DatabaseIdUtil}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control._

/*	The list of database to choose from*/
class DatabaseList(localization : Localization) extends TControlBuilder with TDatabaseList {
  private val menuForeignKeyToFile = new MenuItem(localization.buildForeignKeysFile)
  private val buffer = ObservableBuffer.empty[DatabaseId]
  private val list = new ListView[DatabaseId](buffer) {
  	SplitPane.setResizableWithParent(this, value = false)
  	contextMenu = new ContextMenu(menuForeignKeyToFile)   
    cellFactory = { _ => buildCell() }
  }

  private def buildCell() = new ListCell[DatabaseId] {
    item.onChange { (_, _, _) => 
      text.value = Option(item.value).map(DatabaseIdUtil.databaseIdText).getOrElse("")
    }} 

  def setDatabaseIds(databaseIds: DatabaseIds) : Unit = {
    println("Got new database list:"+databaseIds)
    JFXUtil.bufferSet(buffer, databaseIds.names.sortBy(DatabaseIdUtil.databaseIdText))
  }

  def onDatabaseSelected(use : DatabaseId => Unit) : Unit = 
    JFXUtil.onAction(list, (selectedDatabaseId : DatabaseId, _) => { 
      println("Selected "+DatabaseIdUtil.databaseIdText(selectedDatabaseId))
      use(selectedDatabaseId)
    })

  def onForeignKeyToFile(use : DatabaseId => Unit) : Unit =
  	JFXUtil.onContextMenu(menuForeignKeyToFile, list, {selectedDatabaseId : DatabaseId => 
      println("Selected "+DatabaseIdUtil.databaseIdText(selectedDatabaseId))
      use(selectedDatabaseId)
    })

  def control : Parent = list
}