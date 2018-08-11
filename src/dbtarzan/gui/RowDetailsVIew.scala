package dbtarzan.gui

import scalafx.scene.layout.{ BorderPane, VBox }
import scalafx.scene.Parent
import scalafx.scene.control.{ Label }
import scalafx.geometry.{ Insets, Pos }
import scalafx.collections.ObservableBuffer
import scalafx.scene.text.TextAlignment 
import dbtarzan.db.{Field, FieldType, Row, DBEnumsText}

class RowDetailsView(dbTable : dbtarzan.db.Table) extends TControlBuilder {
    trait TextControl { def control : Parent; def setText(text : String)}
    class LabelTextControl(label : Label) extends TextControl {  def control : Parent = label; def setText(text : String) { label.text = text}}
    
    private val names = dbTable.columnNames
    println("ColumnNames: "+names.map(f => f.name+ DBEnumsText.fieldTypeToText(f.fieldType)))

    private val controls = names.map(f => buildControlForField(f.fieldType))
    private val cellsContainer = new VBox {
        padding = Insets(5)
        spacing = 5
        alignment = Pos.TopLeft
        fillWidth = true
        children = names.zip(controls).map({ case (field, control) => buildCell(field, control)})
    }
    

    private def buildCell(field: Field, control: LabelTextControl) : Parent = new VBox {
        children = List(new Label(field.name), control.control)   
        fillWidth = true
    }

    private def buildControlForField(fieldType : FieldType) = new LabelTextControl(
        new  Label() {
            wrapText = (fieldType == FieldType.STRING)
            stylesheets = List("rowDetailsLabel.css")
            maxWidth = Double.MaxValue
        }
    )

    def displayRow(row : Row) : Unit = {
        row.values.zip(controls).foreach({ case (value, control) => control.setText(value)})
    }

    def control : Parent = cellsContainer
}