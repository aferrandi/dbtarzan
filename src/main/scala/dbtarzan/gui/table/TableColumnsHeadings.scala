package dbtarzan.gui.table

import dbtarzan.db._
import scalafx.scene.image.Image

object TableColumnsStates {
  val PRIMARYKEY_STATE: Int = 1
  val FOREIGNKEY_STATE: Int = 2
  val BOTHKEYS_STATE: Int = PRIMARYKEY_STATE + FOREIGNKEY_STATE
}

case class HeadingTextAndIcon(index: Int, text: String, icon : Option[Image])

/* produces headings for the UI table depending by the primary and foreign keys of which the columns are part */
class TableColumnsHeadings(columnNames : List[Field]) {
  val attributes = new TableColumnsAttributes(columnNames.map(_.name))

  def addPrimaryKeys(keys : PrimaryKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = keys.keys.flatMap(_.fields)
    addKeys(fieldNames, TableColumnsStates.PRIMARYKEY_STATE)
  }

  def addForeignKeys(newForeignKeys : ForeignKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = newForeignKeys.keys.filter(_.direction == ForeignKeyDirection.STRAIGHT).flatMap(_.from.fields)
    addKeys(fieldNames, TableColumnsStates.FOREIGNKEY_STATE)
  }

  private def addKeys(fieldNames: List[String], state: Int): List[HeadingTextAndIcon] = {
    attributes.addKeys(fieldNames, state).map(
      heading => HeadingTextAndIcon(heading.index, heading.text, TableColumnsIcons.bitsetToIcon(heading.attributes))
    )
  }
}