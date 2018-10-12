package dbtarzan.gui

import scala.collection.mutable.Map
import scala.collection.immutable.BitSet
import dbtarzan.db._

object TableColumnsHeadings {
  val PRIMARYKEY_SYMBOL = "\uD83D\uDD11"
  val FOREIGNKEY_SYMBOL = "\u26bf"
  val PRIMARYKEY_STATE = 1
  val FOREIGNKEY_STATE = 2
}

case class HeadingText(index: Int, text: String)

/* produces headings for the UI table depending by the primary and foreign keys of which the columns are part */ 
class TableColumnsHeadings(columnNames : List[Field]) {
  private val columnNamesLowerCase = columnNames.map(_.name.toLowerCase)
  private val indexByKey = columnNamesLowerCase.zipWithIndex.toMap
  private val keysAttributes = Map(columnNamesLowerCase.map(n => (n, BitSet.empty)) : _*)
    
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
      keysAttributes.update(key, keysAttributes.get(key).get + state)
    def toHeadingText(kl : KeyAndLabel) = 
      HeadingText(indexByKey.get(kl.key).get, bitsetToText(kl.key) + " " + kl.label)

    val keysAndLabels = fieldNames.map(n => KeyAndLabel(n.toLowerCase, n))
    keysAndLabels.foreach(kl => addKeyAttribute(kl.key))
    keysAndLabels.map(toHeadingText)
  }

  private def bitsetToText(fieldName: String) : String = {
    def toText(bit : Int) : String = bit match {
        case TableColumnsHeadings.PRIMARYKEY_STATE => TableColumnsHeadings.PRIMARYKEY_SYMBOL
        case TableColumnsHeadings.FOREIGNKEY_STATE => TableColumnsHeadings.FOREIGNKEY_SYMBOL
    }
    keysAttributes.get(fieldName.toLowerCase).get.toList.map(toText).mkString("")
  }
}