package dbtarzan.gui.foreignkeys

import akka.actor.ActorRef
import dbtarzan.db._
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.{OnChangeSafe, OrderedListView, TComboStrategy}
import dbtarzan.localization.Localization
import dbtarzan.messages.QueryColumnsForForeignKeys
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Parent
import scalafx.scene.control.{ComboBox, Label, ListCell, TextField}
import scalafx.scene.layout.{ColumnConstraints, GridPane}
import scalafx.scene.paint.Color

/* To edit a single foreign keys. Every change gets propagated to the other parts of the editor */
class SingleEditor(
  dbActor: ActorRef,
  databaseId: DatabaseId,  
  tableNames: TableNames,
  localization: Localization
  ) extends TControlBuilder {
  val safe = new OnChangeSafe()
  private val showText: Option[String] => Label = (value: Option[String]) => new Label {
    textFill = Color.Black
    text = value.getOrElse("")
  }
  private val comboStrategy = new TComboStrategy[String] {
    override def removeFromCombo(comboBuffer: ObservableBuffer[String], item: String): Unit = comboBuffer -= item
    override def addToCombo(comboBuffer: ObservableBuffer[String], item: String): Unit = comboBuffer += item
  }
  private val orderedListColumnsFrom = new OrderedListView[String](localization.add, showText, comboStrategy)
  private val orderedListColumnsTo = new OrderedListView[String](localization.add, showText, comboStrategy)
  private val chosenTableFromProperty = buildChosenTableProperty(orderedListColumnsFrom)
  private val chosenTableToProperty =  buildChosenTableProperty(orderedListColumnsTo)
  private val tableNamesBuffer = ObservableBuffer(tableNames.tableNames)
  private val comboTableFrom = buildComboTable(localization.tableFrom, chosenTableFromProperty) 
  private val comboTableTo = buildComboTable(localization.tableTo, chosenTableToProperty) 
  
  val txtName = new TextField {
    text = ""
  }
  private var editorDisabled = BooleanProperty(true)

  private def buildChosenTableProperty(orderedListColumns : OrderedListView[String]) = new ObjectProperty[String]() {
    onChange { (_, _, newTable) => Option(newTable).filter(t => t.nonEmpty).foreach(t => {
      orderedListColumns.setListData(List.empty)
      dbActor ! QueryColumnsForForeignKeys(databaseId, t)
    }) }
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
    add(comboTableFrom, 1, 1)
    add(new Label { text = localization.tableTo+":" }, 2, 1)
    add(comboTableTo, 3, 1)
    add(new Label { text = localization.columnsFrom+":" }, 0, 2)
    add(orderedListColumnsFrom.control, 1, 2)
    add(new Label { text = localization.columnsTo+":" }, 2, 2)
    add(orderedListColumnsTo.control, 3, 2)
    padding = Insets(10)
    vgap = 10
    hgap = 10
    disable <==> editorDisabled
  }


  def show(key : AdditionalForeignKey) : Unit = safe.noChangeEventDuring(() => {
    println("show "+key)
    txtName.text = key.name
    chosenTableFromProperty.value = key.from.table
    chosenTableToProperty.value = key.to.table
    orderedListColumnsFrom.setListData(key.from.fields)
    orderedListColumnsTo.setListData(key.to.fields)
    editorDisabled.value = false
  })

  def toKey(): AdditionalForeignKey = {
    val key = AdditionalForeignKey(
      txtName.text(), 
      FieldsOnTable(chosenTableFromProperty.value, orderedListColumnsFrom.listData()),
      FieldsOnTable(chosenTableToProperty.value, orderedListColumnsTo.listData())
   )
   key
  }

  def control : Parent = grid

  def onChanged(useKey : AdditionalForeignKey => Unit) : Unit = {  
     txtName.text.onChange(safe.onChange(() => useKey(toKey())))
     List(
      chosenTableFromProperty,
      chosenTableToProperty 
    ).foreach(_.onChange(safe.onChange(() => useKey(toKey()))))
     List(
      orderedListColumnsFrom,
      orderedListColumnsTo 
    ).foreach(_.onChange(_ => safe.onChange(() => useKey(toKey()))))
  }

  private def handleColumnsForTable(tableName : String, columns : Fields, comboTable : ComboBox[String], orderedListColumns : OrderedListView[String]) : Unit = {
    if(comboTable.value.value == tableName) {
      orderedListColumns.setComboData(columns.fields.map(_.name))
    }
  }

  def handleColumns(tableName : String, columns : Fields) : Unit = {
    safe.onChange(() => {
      handleColumnsForTable(tableName, columns, comboTableFrom, orderedListColumnsFrom)
      handleColumnsForTable(tableName, columns, comboTableTo, orderedListColumnsTo)
    })
  }
}

