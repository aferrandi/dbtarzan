package dbtarzan.gui.table

import dbtarzan.db._
import dbtarzan.gui.util.JFXUtil
import scalafx.scene.image.Image

import scala.collection.immutable.BitSet
import scala.collection.mutable

object TableColumnsHeadings {
  val PRIMARYKEY_STATE: Int = 1
  val FOREIGNKEY_STATE: Int = 2
  val BOTHKEYS_STATE: Int = PRIMARYKEY_STATE + FOREIGNKEY_STATE
  val PRIMARYKEY_ICON: Image = JFXUtil.loadIcon("primaryKey.png")
  val FOREIGNKEY_ICON: Image = JFXUtil.loadIcon("foreignKey.png")
  val BOTHKEYS_ICON: Image = JFXUtil.loadIcon("bothKeys.png")
}

case class HeadingText(index: Int, text: String, icon : Option[Image])

/* produces headings for the UI table depending by the primary and foreign keys of which the columns are part */ 
class TableColumnsHeadings(columnNames : List[Field]) {
  private val columnNamesLowerCase = columnNames.map(_.name.toLowerCase)
  private val indexByKey = columnNamesLowerCase.zipWithIndex.toMap
  private val keysAttributes = mutable.Map(columnNamesLowerCase.map(n => (n, BitSet.empty)) : _*)
    
  def addPrimaryKeys(keys : PrimaryKeys) : List[HeadingText] = {
    val fieldNames = keys.keys.flatMap(_.fields)
    addKeys(fieldNames, TableColumnsHeadings.PRIMARYKEY_STATE)
  }

  def addForeignKeys(newForeignKeys : ForeignKeys) : List[HeadingText] = {
    val fieldNames = newForeignKeys.keys.filter(_.direction == ForeignKeyDirection.STRAIGHT).flatMap(_.from.fields)
    addKeys(fieldNames, TableColumnsHeadings.FOREIGNKEY_STATE)
  }

  case class KeyAndLabel(key : String, label: String)

  private def addKeys(fieldNames : List[String], state : Int) : List[HeadingText] = {
    def addKeyAttribute(key : String) : Unit = 
      keysAttributes.update(key, keysAttributes(key) + state)
    def toHeadingText(kl : KeyAndLabel) = 
      HeadingText(indexByKey(kl.key), kl.label, bitsetToIcon(kl.key))

    val keysAndLabels = fieldNames.map(n => KeyAndLabel(n.toLowerCase, n))
    keysAndLabels.foreach(kl => addKeyAttribute(kl.key))
    keysAndLabels.map(toHeadingText)
  }

  private def bitsetToIcon(fieldName: String) : Option[Image] = {
    def toIcon(total : Int) : Option[Image] = total match {
        case TableColumnsHeadings.PRIMARYKEY_STATE => Some(TableColumnsHeadings.PRIMARYKEY_ICON)
        case TableColumnsHeadings.FOREIGNKEY_STATE => Some(TableColumnsHeadings.FOREIGNKEY_ICON)
        case TableColumnsHeadings.BOTHKEYS_STATE => Some(TableColumnsHeadings.BOTHKEYS_ICON)
        case _ => None
    }
    val bitset = keysAttributes(fieldName.toLowerCase)
    toIcon(bitset.sum) 
  }
}