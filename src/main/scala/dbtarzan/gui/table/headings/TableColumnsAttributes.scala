package dbtarzan.gui.table.headings

import scala.collection.immutable.BitSet
import scala.collection.mutable

case class HeadingText(key: String, attributes: BitSet)

class TableColumnsAttributes(keys : List[String]) {
  private val keysAttributes = mutable.Map(keys.map(n => (n, BitSet.empty)) *)

  def addKeys(keysToAdd : List[String], state : Int) : List[HeadingText] = {
    try {
      keysToAdd.foreach(key => addKeyAttribute(key, state))
      keysToAdd.map(toHeadingText)
    } catch {
      case e: Exception =>
        throw new Exception("Finding key in keysAttributes " + keysAttributes.keys.mkString(",") + " got", e)
    }
  }

  private def addKeyAttribute(key: String, state: Int): BitSet = {
    val updateAttributes = keysAttributes(key) + state
    keysAttributes.update(key, updateAttributes)
    updateAttributes
  }

  private def toHeadingText(key : String) =
    HeadingText(key, keysAttributes(key))
}
