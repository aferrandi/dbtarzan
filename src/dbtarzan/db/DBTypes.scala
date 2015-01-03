package dbtarzan.db

abstract sealed class FieldType

object FieldType {
object STRING extends FieldType
object INT extends FieldType
object FLOAT extends FieldType
}



case class TableDescription(name: String, origin : Option[String], notes: Option[String])

case class TableNames(tableNames : List[String])

case class Field(name : String,  fieldType : FieldType)

case class Fields(fields : List[Field])

case class FieldsOnTable(table : String, fields : List[String])

case class ForeignKey(name: String, from : FieldsOnTable, to: FieldsOnTable)

case class ForeignKeys(keys : List[ForeignKey])

case class FieldWithValue(field : String, value : String)

case class Row(values : List[String])

case class Rows(rows : List[Row])

case class Constraint(text : String)

case class FollowKey(columns : List[Field], key : ForeignKey, rows : List[Row])
