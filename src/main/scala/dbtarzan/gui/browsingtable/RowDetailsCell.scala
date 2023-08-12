package dbtarzan.gui.browsingtable

import dbtarzan.db.{Field, FieldType}
import scalafx.scene.control.{Label, TextArea, TextField, TextInputControl}
import scalafx.scene.layout.VBox

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
    val content: VBox = new VBox {
        children = buildChildren()
        fillWidth = true
    }

    private def buildChildren() =
        List(new Label(field.name), textControl)


    private def buildControl() : TextInputControl = if (alreadyMultiline) {
      buildControlMultiLine()
    } else {
      buildControlSingleLine()
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
        Option(s).exists(_.contains('\n'))

    def showText(value : String|Int|Double) : Unit = {
      textControl.text = field.fieldType match {
        case FieldType.STRING => {
          val text = value.asInstanceOf[String]
          if (! alreadyMultiline && isMultiline(text))
            replaceWithMultilineControl()
          text
        }
        case FieldType.INT => value.toString
        case FieldType.FLOAT => value.toString
      }
    }
}
