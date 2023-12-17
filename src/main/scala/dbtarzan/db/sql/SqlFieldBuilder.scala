package dbtarzan.db.sql

import dbtarzan.db.*

class SqlFieldBuilder(columns : List[Field], attributes : QueryAttributes) {
  val typeByName: Map[String, FieldType] = columns.map(field => (field.name.toUpperCase, field.fieldType)).toMap

  def buildFieldText(fieldWithValue : FieldWithValue): String = {
    val field = fieldWithValue.field
    typeByName.get(field.toUpperCase) match {
      case Some(fieldType) => buildFieldValueText(fieldWithValue, fieldType)
      case None => throw new Exception("field "+field+" not found in column types "+typeByName.keys)
    }
  }

  private def buildFieldValueText(fieldWithValue : FieldWithValue, fieldType : FieldType): String = {
    val fieldRaw = fieldWithValue.field
    val field = QueryAttributesApplier.from(attributes).applyDelimiters(fieldRaw)
    val fieldValue = fieldWithValue.value
    if(fieldValue == null)
      field + " IS NULL"
    else if(fieldType == FieldType.STRING)
      field + "='" + fieldValue + "'"
    else
      field + "=" +  fieldValue
  }
}
