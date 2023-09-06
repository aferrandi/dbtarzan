package dbtarzan.gui.rowdetails

import dbtarzan.db.{Field, FieldType}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.scene.Node
import scalafx.scene.control.*
import scalafx.scene.layout.{BorderPane, VBox}

/*
   Displays a single field of the table, as name and value dispoesed vertically.
   The value is always a string, therefore a TextField/TextArea compoent is used.
   A TextField component is used in all cases but with multiline texts, where a TextArea is used.
   The problem is that it is unknown when this compenent is built if a text field has multiple lines
   therefore TextField is used until a multiline value is received; when this happens the TextField is
   replaced with a TextArea
*/
class RowDetailsCellNumber(field: Field) extends TRowDetailsCell {
  /* true if the field has been recognized as multiline text, so we don't need to check again if it is multilone */
  private val text = new TextField() {
    editable = false
  }
  def showText(value : String|Int|Double) : Unit = {
    text.text = Option(value).map(_.toString).getOrElse("")
  }

  def control: Node = text
}
