package dbtarzan.gui.table

import dbtarzan.db._
import dbtarzan.gui.table.headings.{TableColumnsAttributes, TableColumnsIWithIndex, TableColumnsIcons, TableColumnsStates}
import scalafx.scene.image.Image



case class HeadingTextAndIcon(index: Int, text: String, icon : Option[Image])

/* produces headings for the UI table depending by the primary and foreign keys of which the columns are part */
class TableColumnsHeadings(columnNames : List[Field]) {
  private val columnsWithIndex = new TableColumnsIWithIndex(columnNames.map(_.name))
  private val attributes = new TableColumnsAttributes(columnsWithIndex.keys())

  def addPrimaryKeys(keys : PrimaryKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = keys.keys.flatMap(_.fields)
    addKeys(fieldNames, TableColumnsStates.PRIMARYKEY_STATE)
  }

  def addForeignKeys(newForeignKeys : ForeignKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = newForeignKeys.keys.filter(_.direction == ForeignKeyDirection.STRAIGHT).flatMap(_.from.fields)
    addKeys(fieldNames, TableColumnsStates.FOREIGNKEY_STATE)
  }

  private def addKeys(fieldNames: List[String], state: Int): List[HeadingTextAndIcon] =
    attributes.addKeys(fieldNames.map(TableColumnsIWithIndex.keyFromColumnName), state).map(
      heading => HeadingTextAndIcon(columnsWithIndex.indexOf(heading.key), columnsWithIndex.nameOf(heading.key), TableColumnsIcons.bitsetToIcon(heading.attributes))
    )
}