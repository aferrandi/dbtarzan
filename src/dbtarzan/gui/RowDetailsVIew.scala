package dbtarzan.gui

import scalafx.scene.layout.VBox
import scalafx.scene.Parent
import scalafx.scene.control.{ Label, TextArea, ScrollPane, TextField, TextInputControl }
import scalafx.geometry.{ Insets, Pos }
import dbtarzan.db.{Field, FieldType, Row}


/* 
   Displays a single field of the table, as name and value dispoesed vertically. 
   The value is always a string, therefore a TextField/TextArea compoent is used.
   A TextField component is used in all cases but with multiline texts, where a TextArea is used.
   The problem is that it is unknown when this compenent is built if a text field has multiple lines
   therefore TextField is used until a multiline value is received; when this happens the TextField is 
   replaced with a TextArea
*/
class RowDetailsCell(field: Field) {
    /* true if the field has been recognized as multiline text, so we don't need to check again if it is multilone */ 
    private var alreadyMultiline = false;
    private var textControl = buildControl() 
    /* a label on top of a text field */
    val content = new VBox {
        children = buildChildren()
        fillWidth = true
    }

    private def buildChildren() =
        List(new Label(field.name), textControl) 
    

    private def buildControl() : TextInputControl = alreadyMultiline match {
        case false => buildControlSingleLine()  
        case true => buildControlMultiLine()
    }  

    private def buildControlSingleLine() : TextInputControl = 
        new TextField() {
            editable = false
        }

    private def buildControlMultiLine() : TextInputControl = 
        new TextArea() {
            editable = false
        }

    private def replaceWithMultilineControl() : Unit = {
        alreadyMultiline = true
        textControl = buildControl()
        content.children = buildChildren()
    }

    private def isMultiline(s : String) : Boolean = 
        Option(s).map(_.contains('\n')).getOrElse(false)

    def showText(text : String) : Unit = {
        if(field.fieldType == FieldType.STRING && !alreadyMultiline && isMultiline(text))
            replaceWithMultilineControl()
        textControl.text = text             
    }
}

/* displays one single line of the table, as a vertical list of the fields */
class RowDetailsView(dbTable : dbtarzan.db.Table, initialRow: Option[Row]) extends TControlBuilder {
    private val names = dbTable.columnNames
    /* the cell components */
    private val cells : List[RowDetailsCell] = names.map({ case (field) => new RowDetailsCell(field)})

    private val cellsContainer = buildCellsContainer()

    initialRow.foreach(displayRow)

    private def buildCellsContainer() = new ScrollPane {
        content = new VBox {
            padding = Insets(5)
            spacing = 5
            alignment = Pos.TopLeft
            fillWidth = true
            children = cells.map(c => c.content) 
        }
        hbarPolicy = ScrollPane.ScrollBarPolicy.Never
        /* need a vertcal scrollbar to show all the fields if the row is very long */ 
        vbarPolicy = ScrollPane.ScrollBarPolicy.AsNeeded
        fitToWidth = true
    }

    
    def displayRow(row : Row) : Unit = {
        row.values.zip(cells).foreach({ case (value, cell) => cell.showText(value)})
    }

    def control : Parent = cellsContainer
}