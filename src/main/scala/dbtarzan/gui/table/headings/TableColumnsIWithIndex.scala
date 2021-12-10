package dbtarzan.gui.table.headings

import scala.collection.mutable

object TableColumnsIWithIndex {
  def keyFromColumnName(columnName : String): String =  columnName.toLowerCase
}

class TableColumnsIWithIndex(columnNames: List[String]) {
  case class KeyAndLabel(key: String, label: String)
  private val keysAndLabels = columnNames.map(n => KeyAndLabel(TableColumnsIWithIndex.keyFromColumnName(n), n))
  private val indexByKey = keysAndLabels.map(_.key).zipWithIndex.toMap
  private val namesByLowerCase: mutable.Map[String, String] = mutable.Map(keysAndLabels.map(n => (n.key, n.label)) : _*)

  def keys(): List[String] = keysAndLabels.map(_.key)

  def indexOf(key: String): Int = indexByKey(key)

  def nameOf(key: String): String = namesByLowerCase(key)
}
