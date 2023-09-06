package dbtarzan.gui.rowdetails

import dbtarzan.db.*
import dbtarzan.gui.browsingtable.ChooseBestPrimaryKey

class RowDetailsApplicant(tableStructure: DBTableStructure) {
  private def chooseBestPrimaryKey = new ChooseBestPrimaryKey(tableStructure.columns.fields)
  var bestPrimaryKey : Option[PrimaryKey] = None

  def addPrimaryKeys(keys : PrimaryKeys): Unit =
    bestPrimaryKey = chooseBestPrimaryKey.bestPrimaryKey(keys)

  def buildRowQueryFromRow(row: Row): Option[DBRowStructure] = {
    val valueByName = tableStructure.columns.fields.map(_.name.toUpperCase()).zip(row.values).toMap
    val filter = bestPrimaryKey.map(
      primaryKey => primaryKey.fields.map(field => FieldWithValue(field, valueByName(field.toUpperCase())))
    )
    filter.map(DBRowStructure(tableStructure.description.name, tableStructure.columns, _, tableStructure.attributes))
  }
}
