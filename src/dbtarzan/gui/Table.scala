package dbtarzan.gui

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, SelectionMode}
import scalafx.beans.property.{StringProperty, ObjectProperty, BooleanProperty}
import scalafx.collections.ObservableBuffer 
import scalafx.scene.control.cell.CheckBoxTableCell
import scalafx.Includes._
import dbtarzan.config.{ Config, ConfigReader }
import dbtarzan.db.{Field, Row, Rows}
import dbtarzan.messages._
import akka.actor.ActorRef

/**
  The GUI piece showing the content of a database table in a GUI table
*/
class Table(dbActor: ActorRef, id : TableId, dbTable : dbtarzan.db.Table){

  val names = dbTable.columnNames
  println("ColumnNames: "+names)
  val buffer = ObservableBuffer.empty[JFXRow]
  val selected = new SelectedRows()
  val fromRow = new JFXRowFromRow(selected)
  val table = buildTable()
  dbActor ! QueryRows(id, dbTable.sql, 500) 
  dbActor ! QueryForeignKeys(id)

  def buildTable() = new TableView[JFXRow](buffer) {
    columns += buildSelectColumn()
    columns ++= names.zipWithIndex.map({ case (field, i) => buildColumn(field, i) })
    editable = true
    selectionModel().selectionMode() = SelectionMode.MULTIPLE
  }

  def buildColumn(field : Field, i : Int) = new TableColumn[JFXRow,String]() {
		text = field.name
		cellValueFactory = { _.value.values(i) }
    prefWidth = 180
	}.delegate

  def buildSelectColumn() =  {
    val selectColumn = new TableColumn[JFXRow, java.lang.Boolean] {
        text = ""
        cellValueFactory = { _.value.selected.delegate  }
        prefWidth = 40
        editable = true
    }
    println("Select column created")
    selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn))
    selectColumn
  }

  def addRows(rows : Rows) : Unit = 
    buffer ++= fromRow(rows, names)
  def getId = id
}
