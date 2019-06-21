package dbtarzan.gui.foreignkeys

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView }
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.{ StringProperty }
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ ForeignKey, ForeignKeyDirection, FieldsOnTable }
import dbtarzan.messages._
import dbtarzan.messages.Logger
import dbtarzan.localization.Localization
import dbtarzan.gui.TControlBuilder


/** The GUI table control showing the description of the columns of a database table */
class ForeignKeysTable(guiActor : ActorRef, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val buffer = ObservableBuffer.empty[ForeignKey]
  /* the table */
  private val table = buildTable()

  private var lastSelectedIndex : Option[Int] = None
  table.selectionModel().selectedIndex.onChange((_, _, idx) => 
    Some(idx.intValue()).filter(i => i >= 0).foreach(i => lastSelectedIndex = Some(i))
  )


  /* builds table with the two columns (name and description) */ 
  def buildTable() = new TableView[ForeignKey](buffer) {
    columns ++= List ( nameColumn(), tableFromColumn(), tableToColumn(), foreignKeysFromColumn(), foreignKeysToColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }


   /* the column with the name of the foreign key */
  private def nameColumn() = new TableColumn[ForeignKey, String] {
    text = localization.name
    cellValueFactory = { x => new StringProperty(x.value.name) }
    resizable = true
  }

   /* the column with the name of the database field */
  private def tableFromColumn() = new TableColumn[ForeignKey, String] {
    text = localization.tableFrom
    cellValueFactory = { x => new StringProperty(x.value.from.table) }
    resizable = true
  }

  private def tableToColumn() = new TableColumn[ForeignKey, String] {
    text = localization.tableTo
    cellValueFactory = { x => new StringProperty(x.value.to.table) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def foreignKeysFromColumn() = new TableColumn[ForeignKey, String] {
    text = localization.columnsFrom
    cellValueFactory = { x => new StringProperty(x.value.from.fields.mkString(" ")) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def foreignKeysToColumn() = new TableColumn[ForeignKey, String] {
    text = localization.columnsTo
    cellValueFactory = { x => new StringProperty(x.value.to.fields.mkString(" ")) }
    resizable = true
  }

  /* adds the database rows (the database table fields) to the table */
  def addRows(additionalKeys : List[ForeignKey]) : Unit = 
    buffer ++= additionalKeys.filter(_.direction == ForeignKeyDirection.STRAIGHT)
    
  def onSelected(action : ForeignKey => Unit) : Unit =
    table.selectionModel().selectedItem.onChange(
      (_, _, row) => {
        Option(row).foreach(action(_))
    })

  def removeSelected() : Unit = 
    lastSelectedIndex.foreach(i => buffer.remove(i))

  def refreshSelected(key : ForeignKey) : Unit =
    lastSelectedIndex.foreach(i => buffer.update(i, key))

  def addEmptyRow() : Unit = {
    println("Adding row")
    buffer += ForeignKey("<NEW>",  FieldsOnTable("", List.empty),  FieldsOnTable("", List.empty), ForeignKeyDirection.STRAIGHT)
    table.selectionModel().selectLast()
  }

  def control : TableView[ForeignKey] = table

  def currentForeignKeys() : List[ForeignKey] = buffer.toList
}
