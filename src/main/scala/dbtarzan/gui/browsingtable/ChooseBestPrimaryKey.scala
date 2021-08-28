package dbtarzan.gui.browsingtable

import dbtarzan.db.{Field, FieldType, PrimaryKey, PrimaryKeys}

class ChooseBestPrimaryKey(tableFields : List[Field]) {
  val fieldsByName: Map[String, FieldType] = tableFields.map(f => (f.name, f.fieldType)).toMap

  private def noStringFields(key: PrimaryKey) : Boolean =
    !key.fields.map(fieldName => fieldsByName(fieldName)).contains(FieldType.STRING)

  private def points(key: PrimaryKey): Int =
    if (noStringFields(key))
      0
    else
      key.fields.length

  def bestPrimaryKey(keys : PrimaryKeys): Option[PrimaryKey] =
     keys.keys.length match {
      case 0 => None
      case 1 => Some(keys.keys.head)
      case _ => Some(keys.keys.minBy(points))
    }
}
