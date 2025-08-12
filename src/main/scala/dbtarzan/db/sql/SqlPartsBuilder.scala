package dbtarzan.db.sql

import dbtarzan.db.{DBEnumsText, FieldType, FieldValue, FieldWithValue, OrderByField, OrderByFields}

object SqlPartsBuilder {
  def buildFilters(filters : List[String]) : String = {
    if(filters.nonEmpty)
      filters.mkString("WHERE (\n",
        ") AND (\n"
        , ")")
    else
      ""
  }

  def buildOrderByOne(orderByField: OrderByField) : String =
    orderByField.field.name + " " + DBEnumsText.orderByDirectionToText(orderByField.direction)

  def buildOrderBy(orderByFields: OrderByFields) : String = 
    if (orderByFields.fields.nonEmpty)
      "ORDER BY " + orderByFields.fields.map(buildOrderByOne).mkString(", ")
    else
      ""

  def buildFieldValueText(fieldType: FieldType, fieldValue: FieldValue): String =
    if (fieldType == FieldType.STRING)
      "'" + fieldValue + "'"
    else
      fieldValue.toString
}
