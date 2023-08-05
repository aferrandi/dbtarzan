package dbtarzan.gui.info

import dbtarzan.db.Index
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.scene.Parent
import scalafx.scene.layout.VBox

class IndexesInfo(localization : Localization) extends TControlBuilder {

  val content: VBox = new VBox {
    fillWidth = true
  }

  private var rowsAdded = false

  private var tables = List.empty[IndexInfo]

  /* adds the database rows (the database table fields) to the table */
  def addRows(indexes: List[Index]) : Unit = {
    tables = indexes.map(index => new IndexInfo(localization, index))
    content.children = tables.map(t => t.control)
    rowsAdded = true
  }

  def complete() : Boolean = rowsAdded

  def control : Parent = content
}
