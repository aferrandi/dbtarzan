package dbtarzan.gui.foreignkeys

import scalafx.scene.control.{ TextField, Label, ComboBox, ListCell, ListView }
import scalafx.scene.control.cell.CheckBoxListCell
import scalafx.scene.layout.{ GridPane, ColumnConstraints }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{ Insets }
import scalafx.beans.property.{ StringProperty, ObjectProperty, BooleanProperty }
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.gui.util.OnChangeSafe
import dbtarzan.gui.TControlBuilder
import dbtarzan.db.{TableNames, Field, ForeignKey, FieldsOnTable, ForeignKeyDirection, Fields, DatabaseId }
import dbtarzan.messages.QueryColumnsForForeignKeys
import dbtarzan.localization.Localization

class FieldCheckItem(val field: Field) {
  val selected = BooleanProperty(false)
}

/* The list of database to choose from */
class SingleEditor(
  dbActor: ActorRef,
  databaseId: DatabaseId,  
  tableNames: TableNames,
  localization: Localization
  ) extends TControlBuilder {
  val safe = new OnChangeSafe()
  private val chosenTableFromProperty =  new ObjectProperty[String]() {
    onChange { (_, _, newValue) => dbActor ! QueryColumnsForForeignKeys(databaseId, newValue) }
  }
  private val chosenTableToProperty =  new ObjectProperty[String]() {
    onChange { (_, _, newValue) => dbActor ! QueryColumnsForForeignKeys(databaseId, newValue) }
  }
  private val fromColumnsBuffer = ObservableBuffer.empty[FieldCheckItem]    
  private val toColumnsBuffer = ObservableBuffer.empty[FieldCheckItem]    
  private val tableNamesBuffer = ObservableBuffer(tableNames.tableNames)
  val cboTableFrom = buildComboTable(localization.tableFrom, chosenTableFromProperty) 
  val cboTableTo = buildComboTable(localization.tableTo, chosenTableToProperty) 
  val clsColumnsFrom = buildCheckList(fromColumnsBuffer)
  val clsColumnsTo = buildCheckList(toColumnsBuffer) 
  val txtName = new TextField {
    text = ""
  }

  private def buildComboTable(name : String, chosenTableProperty: ObjectProperty[String]) = new ComboBox[String] {
      promptText.value = name
      items = tableNamesBuffer
      editable = false
      cellFactory = { _ => buildTableCell() }
      buttonCell =  buildTableCell()
      value <==> chosenTableProperty
  }
 
  private def buildTableCell() = new ListCell[String] {
    item.onChange { 
      (_, _, value) => {
          text = Option(value).getOrElse("") 
        }
      }
  } 	

/* all fields in a table (with the table name)n*/
// case class FieldsOnTable(table : String, fields : List[String])
/* a foreign key is a relation between two tables. It has a name and matches fields on the two tables (can clearly be more than one) */
// case class ForeignKey(name: String, from : FieldsOnTable, to: FieldsOnTable, direction : ForeignKeyDirection)

  def buildCheckList(columnsBuffer : ObservableBuffer[FieldCheckItem]) = new  ListView[FieldCheckItem] {
        prefHeight=250
        items = columnsBuffer
        cellFactory = CheckBoxListCell.forListView(_.selected)
  }

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {},
      new ColumnConstraints() {},
      new ColumnConstraints() {
       // hgrow = Priority.ALWAYS
      })
    add(new Label { text = localization.name+":" }, 0, 0)
    add(txtName, 1, 0)
    add(new Label { text = localization.tableFrom+":" }, 0, 1)
    add(cboTableFrom, 1, 1)
    add(new Label { text = localization.columnsFrom+":" }, 2, 1)
    add(clsColumnsFrom, 3, 1)
    add(new Label { text = localization.tableTo+":" }, 0, 2)
    add(cboTableTo, 1, 2)
    add(new Label { text = localization.columnsTo+":" }, 2, 2)
    add(clsColumnsTo, 3, 2)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }


  def show(key : ForeignKey) : Unit = safe.noChangeEventDuring(() => {
    txtName.text = key.name
    chosenTableFromProperty.value = key.from.table
    chosenTableToProperty.value = key.to.table
  })

  private def extractCheckedFields(checkedFields : ObservableBuffer[FieldCheckItem]) : List[String] =
    checkedFields.toList.filter(_.selected.value).map(_.field.name)

  def toKey() = {
    ForeignKey(
      txtName.text(), 
      FieldsOnTable(chosenTableFromProperty.value, extractCheckedFields(fromColumnsBuffer)),
      FieldsOnTable(chosenTableToProperty.value, extractCheckedFields(toColumnsBuffer)),
      ForeignKeyDirection.STRAIGHT
   )}

  def control : Parent = grid

  def onChanged(useKey : ForeignKey => Unit) : Unit = {  
    List(
      fromColumnsBuffer,
      toColumnsBuffer 
    ).flatMap(_.map(_.selected)).foreach(_.onChange((_,_,_) => safe.onChange(() => useKey(toKey()))))
  }

  def handleColumns(tableName : String, columns : Fields) : Unit = {
    if(cboTableFrom.value == tableName) {
      fromColumnsBuffer.clear() 
      fromColumnsBuffer ++= columns.fields.map(new FieldCheckItem(_));
    }
    if(cboTableTo.value == tableName) {
      toColumnsBuffer.clear() 
      toColumnsBuffer ++= columns.fields.map(new FieldCheckItem(_))
    }
  }


}

