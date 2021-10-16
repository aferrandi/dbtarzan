package dbtarzan.gui.info;

import akka.actor.ActorRef
import dbtarzan.db.{Index, OrderByDirection}
import dbtarzan.gui.TControlBuilder
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control.{TableColumn, TableView}

class IndexInfo(dbActor : ActorRef, guiActor : ActorRef, localization : Localization) extends TControlBuilder {
  case class TableLine(indexName: String, fieldName: String, direction: Option[OrderByDirection])

  private val log = new Logger(guiActor)
  private val buffer = ObservableBuffer.empty[TableLine]
  /* the table */
  private val table = buildTable()
  private var rowsAdded = false

  /* builds table with the two columns (name and description) */
  def buildTable(): TableView[TableLine] = new TableView[TableLine](buffer) {
    columns ++= List ( indexNameColumn(), fieldNameColumn(), directionColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }

  /* the column with the name of the database field */
  private def indexNameColumn() = new TableColumn[TableLine, String] {
    text = localization.name
    cellValueFactory = { x => new StringProperty(x.value.indexName) }
    resizable = true
  }

  private def fieldNameColumn() = new TableColumn[TableLine, String] {
    text = localization.field
    cellValueFactory = { x => new StringProperty(x.value.fieldName) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def directionColumn() = new TableColumn[TableLine, String] {
    text = localization.direction
    cellValueFactory = { x => new StringProperty(directionToText(x.value.direction)) }
    resizable = true
  }

  private def directionToText(direction: Option[OrderByDirection]): String =
    direction match {
      case Some(OrderByDirection.ASC) => "ASC"
      case Some(OrderByDirection.DESC) => "DESC"
      case _ => ""
    }

  /* adds the database rows (the database table fields) to the table */
  def addRows(indexes: List[Index]) : Unit = {
    val lines = indexes.flatMap(index => index.fields.map(field => TableLine(index.name, field.name, field.direction)))
    buffer ++= lines
    rowsAdded = true
  }

  def complete() : Boolean = rowsAdded

  def control : Parent = table
}