package dbtarzan.gui.table

import dbtarzan.db._
import scalafx.scene.image.Image
import scala.collection.mutable

object TableColumnsStates {
  val PRIMARYKEY_STATE: Int = 1
  val FOREIGNKEY_STATE: Int = 2
  val BOTHKEYS_STATE: Int = PRIMARYKEY_STATE + FOREIGNKEY_STATE
}

case class HeadingTextAndIcon(index: Int, text: String, icon : Option[Image])

/* produces headings for the UI table depending by the primary and foreign keys of which the columns are part */
class TableColumnsHeadings(columnNames : List[Field]) {
  case class KeyAndLabel(key: String, label: String)
  private val keysAndLabels = columnNames.map(_.name).map(n => KeyAndLabel(n.toLowerCase, n))
  private val indexByKey = keysAndLabels.map(_.key).zipWithIndex.toMap
  private val namesByLowerCase: mutable.Map[String, String] = mutable.Map(keysAndLabels.map(n => (n.key, n.label)) : _*)
  private val attributes = new TableColumnsAttributes(keysAndLabels.map(_.key))

  def addPrimaryKeys(keys : PrimaryKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = keys.keys.flatMap(_.fields)
    addKeys(fieldNames, TableColumnsStates.PRIMARYKEY_STATE)
  }

  def addForeignKeys(newForeignKeys : ForeignKeys) : List[HeadingTextAndIcon] = {
    val fieldNames = newForeignKeys.keys.filter(_.direction == ForeignKeyDirection.STRAIGHT).flatMap(_.from.fields)
    addKeys(fieldNames, TableColumnsStates.FOREIGNKEY_STATE)
  }

  private def addKeys(fieldNames: List[String], state: Int): List[HeadingTextAndIcon] = {
    attributes.addKeys(fieldNames.map(_.toLowerCase()), state).map(
      heading => HeadingTextAndIcon(indexByKey(heading.key), namesByLowerCase(heading.key), TableColumnsIcons.bitsetToIcon(heading.attributes))
    )
  }
}