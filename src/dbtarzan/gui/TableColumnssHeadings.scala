package dbtarzan.gui

import scala.collection.mutable.Map
import scala.collection.immutable.BitSet
import dbtarzan.db._

object TableColumnssHeadings {
  val PRIMARYKEY_SYMBOL = "\uD83D\uDD11"
  val FOREIGNKEY_SYMBOL = "\u26bf"
  val PRIMARYKEY_STATE = 1
  val FOREIGNKEY_STATE = 2
}

case class HeadingText(index: Int, text: String)

class TableColumnssHeadings(columnNames : List[Field]) {
  private val columnNamesLowerCase = columnNames.map(_.name.toLowerCase)
  private val indexByKey = columnNamesLowerCase.zipWithIndex.toMap
  private val keysAttributes = Map(columnNamesLowerCase.map(n => (n, BitSet.empty)) : _*)
    
  def addPrimaryKeys(keys : List[PrimaryKey]) : List[HeadingText] = {
    val fieldNames = keys.flatMap(_.fields)
    addKeys(fieldNames, TableColumnssHeadings.PRIMARYKEY_STATE)
  }

  def addForeignKeys(newForeignKeys : ForeignKeys) : List[HeadingText] = {
    val fieldNames = newForeignKeys.keys.filter(_.direction == ForeignKeyDirection.STRAIGHT).flatMap(_.from.fields)
    addKeys(fieldNames, TableColumnssHeadings.FOREIGNKEY_STATE)
  }

  private def addKeys(fieldNames : List[String], state : Int) : List[HeadingText] = {
    fieldNames.map(_.toLowerCase).foreach(lowerCaseName => {
        keysAttributes.update(lowerCaseName, keysAttributes.get(lowerCaseName).get + state)
    })
    fieldNames.map({ case name => HeadingText(indexByKey.get(name.toLowerCase).get, bitsetToText(name.toLowerCase) + " " + name) })
  }

  private def bitsetToText(fieldName: String) : String = {
    def toText(bit : Int) : String = bit match {
        case TableColumnssHeadings.PRIMARYKEY_STATE => TableColumnssHeadings.PRIMARYKEY_SYMBOL
        case TableColumnssHeadings.FOREIGNKEY_STATE => TableColumnssHeadings.FOREIGNKEY_SYMBOL
    }
    keysAttributes.get(fieldName.toLowerCase).get.toList.map(toText).mkString("")
  }
}