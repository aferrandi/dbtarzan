package dbtarzan.gui.info

import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView }
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.{ StringProperty }
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{ Fields, Field }
import dbtarzan.messages._
import dbtarzan.messages.Logger
import dbtarzan.localization.Localization
import dbtarzan.gui.TControlBuilder


/** The GUI table control showing the description of the columns of a database table */
class ColumnsTable(fields: Fields, guiActor : ActorRef, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val buffer = ObservableBuffer.empty[Field]
  /* the table */
  private val table = buildTable()
  addRows()


  /* builds table with the two columns (name and description) */ 
  def buildTable() = new TableView[Field](buffer) {
    columns ++= List ( nameColumn(), descriptionColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }

   /* the column with the name of the database field */
  private def nameColumn() = new TableColumn[Field, String] {
    text = localization.field
    cellValueFactory = { x => new StringProperty(x.value.name) }
    resizable = true
  }

  /* the column with the description of the database field */
  private def descriptionColumn() = new TableColumn[Field, String] {
    text = localization.description
    cellValueFactory = { x => new StringProperty(x.value.typeDescription) }
    resizable = true
  }

  /* adds the database rows (the database table fields) to the table */
  def addRows() : Unit = 
    buffer ++= fields.fields
    

  def control : Parent = table
}
