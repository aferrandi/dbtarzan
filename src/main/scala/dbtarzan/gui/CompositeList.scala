package dbtarzan.gui

import dbtarzan.db.CompositeId
import dbtarzan.gui.interfaces.{TCompositeList, TControlBuilder}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import dbtarzan.messages.CompositeIds
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{ListCell, ListView, SplitPane}

class CompositeList(localization: Localization) extends TControlBuilder with TCompositeList {
  private val buffer = ObservableBuffer.empty[CompositeId]
  private val list = new ListView[CompositeId](buffer) {
    SplitPane.setResizableWithParent(this, value = false)
    cellFactory = { _ => buildCell() }
  }

  private def buildCell() = new ListCell[CompositeId] {
    item.onChange { (_, _, _) =>
      text.value = Option(item.value).map(compositeId => compositeId.compositeName).getOrElse("")
    }
  }

  def setCompositeIds(compositeIds: CompositeIds): Unit = {
    println("Got new composite list:" + compositeIds.compositeIds)
    JFXUtil.bufferSet(buffer, compositeIds.compositeIds.sortBy(_.compositeName))
  }

  def onDatabaseSelected(use: CompositeId => Unit): Unit =
    JFXUtil.onAction(list, (selectedCompositeId: CompositeId, _) => {
      println("Selected " + selectedCompositeId.compositeName)
      use(selectedCompositeId)
    })


  def control: Parent = list
}
