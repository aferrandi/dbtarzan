package dbtarzan.gui

import dbtarzan.db.DatabaseId
import dbtarzan.gui.interfaces.{TControlBuilder, TDatabaseList}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.{DatabaseIdUtil, DatabaseIds}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color


/*	The list of database to choose from*/
class DatabaseList(localization : Localization) extends TControlBuilder with TDatabaseList {
  private val menuForeignKeyToFile = new MenuItem(localization.buildForeignKeysFile)
  private val buffer = ObservableBuffer.empty[DatabaseId]
  private val databaseIcon: Image = JFXUtil.loadIcon("database.png")
  private val compositeIcon: Image = JFXUtil.loadIcon("composite.png")
  private val list = new ListView[DatabaseId](buffer) {
    SplitPane.setResizableWithParent(this, value = false)
    contextMenu = new ContextMenu(menuForeignKeyToFile)
    cellFactory = (cell, value) => {
      cell.graphic = new Label {
        textFill = Color.Black
        text = DatabaseIdUtil.databaseIdText(value)
        graphic = new ImageView(iconFromDatabaseId(value))
      }
    }
  }

  private def iconFromDatabaseId(databaseId: DatabaseId): Image = databaseId.origin match {
    case Left(_) => databaseIcon
    case Right(_) => compositeIcon
  }

  def setDatabaseIds(databaseIds: DatabaseIds) : Unit = {
    println("Got new database list:" + databaseIds.names.map(DatabaseIdUtil.databaseIdText).mkString(","))
    JFXUtil.bufferSet(buffer, databaseIds.names.sortWith((id1, id2) =>
      sortByOriginThenByName(id1, id2)
    ))
  }

  private def sortByOriginThenByName(id1: DatabaseId, id2: DatabaseId) = {
    if (id1.origin.isRight == id2.origin.isRight)
      DatabaseIdUtil.databaseIdText(id1) < DatabaseIdUtil.databaseIdText(id2)
    else
      id1.origin.isRight
  }

  def onDatabaseSelected(use : DatabaseId => Unit) : Unit =
    JFXUtil.onAction(list, (selectedDatabaseId : DatabaseId, _) => { 
      println("Selected "+DatabaseIdUtil.databaseIdText(selectedDatabaseId))
      use(selectedDatabaseId)
    })

  def onForeignKeyToFile(use : DatabaseId => Unit) : Unit =
    JFXUtil.onContextMenu(menuForeignKeyToFile, list, {(selectedDatabaseId : DatabaseId) =>
      println("Selected "+DatabaseIdUtil.databaseIdText(selectedDatabaseId))
      use(selectedDatabaseId)
    })

  def control : Parent = list
}