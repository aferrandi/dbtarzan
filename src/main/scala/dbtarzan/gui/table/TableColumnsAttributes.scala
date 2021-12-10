package dbtarzan.gui.table

import scala.collection.immutable.BitSet
import scala.collection.mutable

case class HeadingText(key: String, attributes: BitSet)

class TableColumnsAttributes(keys : List[String]) {
  private val keysAttributes = mutable.Map(keys.map(n => (n, BitSet.empty)) : _*)

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
    val bitset = keysAttributes(key) + state
    keysAttributes.update(key, bitset)
    bitset
  }

  private def toHeadingText(key : String) =
    HeadingText(key, keysAttributes(key))
}
