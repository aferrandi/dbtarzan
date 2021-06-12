package dbtarzan.db

object SqlPartsBuilder {
  def buildFilters(filters : List[String]) : String = {
    if(filters.nonEmpty)
      filters.mkString(" WHERE (\n",
        ") AND (\n"
        , ")")
    else
      ""
  }

  def buildOrderByOne(orderByField: OrderByField) : String =
    orderByField.field.name + " " + DBEnumsText.orderByDirectionToText(orderByField.direction)

  def buildOrderBy(orderByFields: OrderByFields) : String = {
    if (orderByFields.fields.nonEmpty)
      " ORDER BY " + orderByFields.fields.map(buildOrderByOne).mkString(", ")
    else
      ""
  }



}
