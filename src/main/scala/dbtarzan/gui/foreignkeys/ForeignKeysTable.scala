package dbtarzan.gui.foreignkeys

import org.apache.pekko.actor.ActorRef
import dbtarzan.db.{VirtualalForeignKey, DatabaseId, FieldsOnTable, SimpleDatabaseId, TableId}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.TableIdLabel
import dbtarzan.localization.Localization
import dbtarzan.messages.Logger
import scalafx.Includes._
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{Button, TableCell, TableColumn, TableView}


object ForeignKeysTable {
    val newRowName = "<NEW>"
}

/** The GUI table control showing the currently edited virtual foreign keys */
class ForeignKeysTable(databaseId: DatabaseId, guiActor : ActorRef, localization : Localization) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val buffer = ObservableBuffer.empty[VirtualalForeignKey]
  /* the table */
  private val table = buildTable()

  private var lastSelectedIndex : Option[Int] = None
  table.selectionModel().selectedIndex.onChange((_, _, idx) => 
    Some(idx.intValue()).filter(i => i >= 0).foreach(i => lastSelectedIndex = Some(i))
  )


  /* builds table with the two columns (name and description) */ 
  def buildTable(): TableView[VirtualalForeignKey] = new TableView[VirtualalForeignKey](buffer) {
    columns ++= List ( nameColumn(), tableFromColumn(), foreignKeysFromColumn(), tableToColumn(), foreignKeysToColumn(), buttonColumn())
    editable = false
    columnResizePolicy = TableView.ConstrainedResizePolicy
  }


   /* the column with the name of the foreign key */
  private def nameColumn() = new TableColumn[VirtualalForeignKey, String] {
    text = localization.name
    cellValueFactory = { x => new StringProperty(x.value.name) }
    resizable = true
  }

   /* the column with the from table of the foreign key */
  private def tableFromColumn() = new TableColumn[VirtualalForeignKey, String] {
    text = localization.tableFrom
    cellValueFactory = { x => new StringProperty(TableIdLabel.toLabel(x.value.from.table)) }
    resizable = true
  }

   /* the column with the to table of the foreign key */
  private def tableToColumn() = new TableColumn[VirtualalForeignKey, String] {
    text = localization.tableTo
    cellValueFactory = { x => new StringProperty(TableIdLabel.toLabel(x.value.to.table)) }
    resizable = true
  }

   /* the column with the from columns of the foreign key */
  private def foreignKeysFromColumn() = new TableColumn[VirtualalForeignKey, String] {
    text = localization.columnsFrom
    cellValueFactory = { x => new StringProperty(x.value.from.fields.mkString(",")) }
    resizable = true
  }

   /* the column with the to columns of the foreign key */
  private def foreignKeysToColumn() = new TableColumn[VirtualalForeignKey, String] {
    text = localization.columnsTo
    cellValueFactory = { x => new StringProperty(x.value.to.fields.mkString(",")) }
    resizable = true
  }

  /* adds new foreign keys to the table */
  def addRows(virtualKeys : List[VirtualalForeignKey]) : Unit =
    buffer ++= virtualKeys
    
  def onSelected(action : VirtualalForeignKey => Unit) : Unit =
    table.selectionModel().selectedItem.onChange(
      (_, _, row) => {
        Option(row).foreach(action(_))
    })


  def refreshSelected(key : VirtualalForeignKey) : Unit =
    lastSelectedIndex.foreach(i => buffer.update(i, key))

  /* adds an empty foreign key */
  def addEmptyRow() : Unit = {
    log.debug("Adding row")
    val emptyFields = FieldsOnTable(TableId(databaseId, SimpleDatabaseId(""), ""), List.empty)
    buffer += VirtualalForeignKey(ForeignKeysTable.newRowName,  emptyFields,  emptyFields)
    table.selectionModel().selectLast()
  }

   /* builds the column on the right with the button to remove the foreign key */
  private def buttonColumn() = new TableColumn[VirtualalForeignKey, Boolean] {
    cellValueFactory = { msg => ObjectProperty(msg.value != null) }
    cellFactory = {
      (_ : TableColumn[VirtualalForeignKey, Boolean]) => buildButtonCell()
    }
    maxWidth = 36
    minWidth = 36
  }

   /* the button to remove the foreign key */
  private def deleteButton(rowIndex : Int) = new Button {
      text = "x"
      stylesheets += "rowButton.css"
      onAction = {
        (_: ActionEvent) => {
          buffer.remove(rowIndex)
        }
      }
    }

   /* the cell of the button to remove the foreign key */
  private def buildButtonCell() = new TableCell[VirtualalForeignKey, Boolean] {
      item.onChange { (_ , _, value) => 
              if(value) {
                graphic = deleteButton(tableRow().index())
              }
              else 
                graphic = null
          }
        }

  def control : TableView[VirtualalForeignKey] = table

  def currentForeignKeys() : List[VirtualalForeignKey] = buffer.toList
}
