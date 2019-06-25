package dbtarzan.gui.foreignkeys

import scalafx.scene.control.{ TextField, Label, ComboBox, ListCell }
import scalafx.scene.layout.{ GridPane, ColumnConstraints }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{ Insets }
import scalafx.beans.property.{ ObjectProperty, BooleanProperty }
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.gui.util.{ OnChangeSafe, OrderedListView, JFXUtil }
import dbtarzan.gui.TControlBuilder
import dbtarzan.db.{TableNames, Field, ForeignKey, FieldsOnTable, ForeignKeyDirection, Fields, DatabaseId }
import dbtarzan.messages.QueryColumnsForForeignKeys
import dbtarzan.localization.Localization

/* The list of database to choose from */
class SingleEditor(
  dbActor: ActorRef,
  databaseId: DatabaseId,  
  tableNames: TableNames,
  localization: Localization
  ) extends TControlBuilder {
  val safe = new OnChangeSafe()
  private val chosenTableFromProperty = buildChosenTableProperty()
  private val chosenTableToProperty =  buildChosenTableProperty()
  private val tableNamesBuffer = ObservableBuffer(tableNames.tableNames)
  private val cboTableFrom = buildComboTable(localization.tableFrom, chosenTableFromProperty) 
  private val cboTableTo = buildComboTable(localization.tableTo, chosenTableToProperty) 
  private val clsColumnsFrom = new OrderedListView[String](x => x, "Add")
  private val clsColumnsTo = new OrderedListView[String](x => x, "Add")
  
  val txtName = new TextField {
    text = ""
  }
  private var editorDisabled = BooleanProperty(true)

  private def buildChosenTableProperty() = new ObjectProperty[String]() {
    onChange { (_, _, newTable) => Option(newTable).filter(t => !t.isEmpty).foreach(t =>
      dbActor ! QueryColumnsForForeignKeys(databaseId, t)
    ) }
  }

  private def buildComboTable(name : String, chosenTableProperty: ObjectProperty[String]) = new ComboBox[String] {
      items = tableNamesBuffer
      editable = false
      cellFactory = { _ => buildTableCell() }
      buttonCell =  buildTableCell()
      maxWidth = Double.MaxValue
      value <==> chosenTableProperty
  }
 
  private def buildTableCell() = new ListCell[String] {
    item.onChange { 
      (_, _, value) => {
          text = Option(value).getOrElse("") 
        }
      }
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
    add(new Label { text = localization.tableTo+":" }, 2, 1)
    add(cboTableTo, 3, 1)
    add(new Label { text = localization.columnsFrom+":" }, 0, 2)
    add(clsColumnsFrom.control, 1, 2)
    add(new Label { text = localization.columnsTo+":" }, 2, 2)
    add(clsColumnsTo.control, 3, 2)
    padding = Insets(10)
    vgap = 10
    hgap = 10
    disable <==> editorDisabled
  }


  def show(key : ForeignKey) : Unit = safe.noChangeEventDuring(() => {
    txtName.text = key.name
    chosenTableFromProperty.value = key.from.table
    chosenTableToProperty.value = key.to.table
    JFXUtil.bufferSet(clsColumnsFrom.listBuffer, key.from.fields)
    JFXUtil.bufferSet(clsColumnsTo.listBuffer, key.to.fields)
    editorDisabled.value = false
  })

  def toKey() = 
    ForeignKey(
      txtName.text(), 
      FieldsOnTable(chosenTableFromProperty.value, clsColumnsFrom.listBuffer.toList),
      FieldsOnTable(chosenTableToProperty.value, clsColumnsTo.listBuffer.toList),
      ForeignKeyDirection.STRAIGHT
   )

  def control : Parent = grid

  def onChanged(useKey : ForeignKey => Unit) : Unit = {  
      txtName.text.onChange(safe.onChange(() => useKey(toKey())))
     List(
      chosenTableFromProperty,
      chosenTableToProperty 
    ).foreach(_.onChange(safe.onChange(() => useKey(toKey()))))
     List(
      clsColumnsFrom,
      clsColumnsTo 
    ).foreach(_.onChange(_ => safe.onChange(() => useKey(toKey()))))
  }

  private def handleColumnsForTable(tableName : String, columns : Fields, cboTable : ComboBox[String], clsColumns : OrderedListView[String]) : Unit = {
    if(cboTable.value.value == tableName) {
      clsColumns.setComboData(columns.fields.map(_.name))
    }
  }

  def handleColumns(tableName : String, columns : Fields) : Unit = {
    handleColumnsForTable(tableName, columns, cboTableFrom, clsColumnsFrom)
    handleColumnsForTable(tableName, columns, cboTableTo, clsColumnsTo)
  }
}

