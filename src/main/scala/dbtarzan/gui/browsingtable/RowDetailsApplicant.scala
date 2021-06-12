package dbtarzan.gui.browsingtable

import dbtarzan.db._

class RowDetailsApplicant(tableStructure: DBTableStructure) {
  private def chooseBestPrimaryKey = new ChooseBestPrimaryKey(tableStructure.columns.fields)
  var bestPrimaryKey : Option[PrimaryKey] = None

  def addPrimaryKeys(keys : PrimaryKeys): Unit =
    bestPrimaryKey = chooseBestPrimaryKey.bestPrimaryKey(keys)

  def buildRowQueryFromRow(row: Row): Option[DBRowStructure] = {
    val valueByName = tableStructure.columns.fields.map(_.name).zip(row.values).toMap
    val filter = bestPrimaryKey.map(
      primaryKey => primaryKey.fields.map(field => FieldWithValue(field, valueByName(field)))
    )
    filter.map(DBRowStructure(tableStructure.description.name, tableStructure.columns, _, tableStructure.attributes))
  }
}
