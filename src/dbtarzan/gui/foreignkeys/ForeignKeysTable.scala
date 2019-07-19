package dbtarzan.gui.foreignkeys

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView, TableCell, Button }
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.beans.property.{ StringProperty, ObjectProperty, BooleanProperty }
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ AdditionalForeignKey, FieldsOnTable }
import dbtarzan.messages._
import dbtarzan.messages.Logger
import dbtarzan.localization.Localization
import dbtarzan.gui.TControlBuilder


/** The GUI table control showing the description of the columns of a database table */
class ForeignKeysTable(guiActor : ActorRef, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val buffer = ObservableBuffer.empty[AdditionalForeignKey]
  /* the table */
  private val table = buildTable()

  private var lastSelectedIndex : Option[Int] = None
  table.selectionModel().selectedIndex.onChange((_, _, idx) => 
    Some(idx.intValue()).filter(i => i >= 0).foreach(i => lastSelectedIndex = Some(i))
  )


  /* builds table with the two columns (name and description) */ 
  def buildTable() = new TableView[AdditionalForeignKey](buffer) {
    columns ++= List ( nameColumn(), tableFromColumn(), foreignKeysFromColumn(), tableToColumn(), foreignKeysToColumn(), buttonColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }


   /* the column with the name of the foreign key */
  private def nameColumn() = new TableColumn[AdditionalForeignKey, String] {
    text = localization.name
    cellValueFactory = { x => new StringProperty(x.value.name) }
    resizable = true
  }

   /* the column with the name of the database field */
  private def tableFromColumn() = new TableColumn[AdditionalForeignKey, String] {
    text = localization.tableFrom
    cellValueFactory = { x => new StringProperty(x.value.from.table) }
    resizable = true
  }

  private def tableToColumn() = new TableColumn[AdditionalForeignKey, String] {
    text = localization.tableTo
    cellValueFactory = { x => new StringProperty(x.value.to.table) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def foreignKeysFromColumn() = new TableColumn[AdditionalForeignKey, String] {
    text = localization.columnsFrom
    cellValueFactory = { x => new StringProperty(x.value.from.fields.mkString(",")) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def foreignKeysToColumn() = new TableColumn[AdditionalForeignKey, String] {
    text = localization.columnsTo
    cellValueFactory = { x => new StringProperty(x.value.to.fields.mkString(",")) }
    resizable = true
  }

  /* adds the database rows (the database table fields) to the table */
  def addRows(additionalKeys : List[AdditionalForeignKey]) : Unit = 
    buffer ++= additionalKeys
    
  def onSelected(action : AdditionalForeignKey => Unit) : Unit =
    table.selectionModel().selectedItem.onChange(
      (_, _, row) => {
        Option(row).foreach(action(_))
    })


  def refreshSelected(key : AdditionalForeignKey) : Unit =
    lastSelectedIndex.foreach(i => buffer.update(i, key))

  def addEmptyRow() : Unit = {
    println("Adding row")
    buffer += AdditionalForeignKey("<NEW>",  FieldsOnTable("", List.empty),  FieldsOnTable("", List.empty))
    table.selectionModel().selectLast()
  }

   /* build the column on the left, that shows the icon (error, warn, info) */
  private def buttonColumn() = new TableColumn[AdditionalForeignKey, Boolean] {
    cellValueFactory = { msg => ObjectProperty(msg.value != null) }
    cellFactory = {
      _ : TableColumn[AdditionalForeignKey, Boolean] => buildButtonCell()
    }
    maxWidth = 36
    minWidth = 36
  }

  private def deleteButton(rowIndex : Int) = new Button {
      text = "x"
      stylesheets += "rowButton.css"
      onAction = {
        (e: ActionEvent) => {
          buffer.remove(rowIndex)
        }
      }
    }

  private def buildButtonCell() = new TableCell[AdditionalForeignKey, Boolean] {
      item.onChange { (_ , _, value) => 
              if(value) {
                graphic = deleteButton(tableRow().index())
              }
              else 
                graphic = null
          }
        }

  def control : TableView[AdditionalForeignKey] = table

  def currentForeignKeys() : List[AdditionalForeignKey] = buffer.toList
}
