package dbtarzan.gui.table

import scala.collection.immutable.BitSet
import scala.collection.mutable

case class HeadingText(index: Int, text: String, attributes: BitSet)

class TableColumnsAttributes(columnNames : List[String]) {
  case class KeyAndLabel(key : String, label: String)

  private val columnNamesLowerCase = columnNames.map(_.toLowerCase)
  private val indexByKey = columnNamesLowerCase.zipWithIndex.toMap
  private val keysAttributes = mutable.Map(columnNamesLowerCase.map(n => (n, BitSet.empty)) : _*)

  def addKeys(fieldNames : List[String], state : Int) : List[HeadingText] = {
    try {
      val keysAndLabels = fieldNames.map(n => KeyAndLabel(n.toLowerCase, n))
      keysAndLabels.foreach(kl => addKeyAttribute(kl.key, state))
      keysAndLabels.map(toHeadingText)
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

  private def toHeadingText(kl: KeyAndLabel) =
    HeadingText(indexByKey(kl.key), kl.label, keysAttributes(kl.key))
}
