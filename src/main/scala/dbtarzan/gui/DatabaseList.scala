package dbtarzan.gui

import dbtarzan.db.{DatabaseId, DatabaseInfo}
import dbtarzan.gui.interfaces.{TControlBuilder, TDatabaseList}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.{DatabaseIdUtil, DatabaseIds, DatabaseInfos}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color


/*	The list of database to choose from*/
class DatabaseList(localization : Localization) extends TControlBuilder with TDatabaseList {
  private val menuForeignKeyToFile = new MenuItem(localization.buildForeignKeysFile)
  private val buffer = ObservableBuffer.empty[DatabaseInfo]
  private val databaseIcon: Image = JFXUtil.loadIcon("database.png")
  private val compositeIcon: Image = JFXUtil.loadIcon("composite.png")
  private val list = new ListView[DatabaseInfo](buffer) {
    SplitPane.setResizableWithParent(this, value = false)
    contextMenu = new ContextMenu(menuForeignKeyToFile)
    cellFactory = (cell, value) => {
      cell.graphic = new Label {
        textFill = Color.Black
        text = DatabaseIdUtil.databaseInfoText(value)
        graphic = new ImageView(iconFromDatabaseId(DatabaseIdUtil.databaseIdFromInfo(value)))
      }
    }
  }

  private def iconFromDatabaseId(databaseId: DatabaseId): Image = databaseId.origin match {
    case Left(_) => databaseIcon
    case Right(_) => compositeIcon
  }

  def setDatabaseInfos(databaseInfos: DatabaseInfos) : Unit = {
    println("Got new database list:" + databaseInfos.infos.map(DatabaseIdUtil.databaseInfoText).mkString(","))
    JFXUtil.bufferSet(buffer, databaseInfos.infos.sortWith((info1, info2) =>
      sortByOriginThenByName(info1, info2)
    ))
  }

  private def sortByOriginThenByName(info1: DatabaseInfo, info2: DatabaseInfo) = {
    if (info1.origin.isRight == info2.origin.isRight)
      DatabaseIdUtil.databaseInfoText(info1) < DatabaseIdUtil.databaseInfoText(info2)
    else
      info1.origin.isRight
  }

  def onDatabaseSelected(use : DatabaseInfo => Unit) : Unit =
    JFXUtil.onAction(list, (selectedDatabaseInfo : DatabaseInfo, _) => {
      println("Selected "+DatabaseIdUtil.databaseInfoText(selectedDatabaseInfo))
      use(selectedDatabaseInfo)
    })

  def onForeignKeyToFile(use : DatabaseInfo => Unit) : Unit =
    JFXUtil.onContextMenu(menuForeignKeyToFile, list, {(selectedDatabaseInfo : DatabaseInfo) =>
      println("Selected "+DatabaseIdUtil.databaseInfoText(selectedDatabaseInfo))
      use(selectedDatabaseInfo)
    })

  def control : Parent = list
}