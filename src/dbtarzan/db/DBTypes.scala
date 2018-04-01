package dbtarzan.db

/* a table: its name, the name of the original table if it comes from another table */
case class TableDescription(name: String, origin : Option[String], notes: Option[String])
/* the tables in a databases */
case class TableNames(tableNames : List[String])
/* a field in a table (name and type) */
case class Field(name : String,  fieldType : FieldType)
/* all fields in a table */
case class Fields(fields : List[Field])
/* all fields in a table (with the table name)n*/
case class FieldsOnTable(table : String, fields : List[String])
/* a foreign key is a relation between two tables. It has a name and matches fields on the two tables (can clearly be more than one) */
case class ForeignKey(name: String, from : FieldsOnTable, to: FieldsOnTable)
/* the foreign keys involving a table */
case class ForeignKeys(keys : List[ForeignKey])
/* a fields with its content in a row */
case class FieldWithValue(field : String, value : String)
/* a row. The values are in the same order as in the table description (FieldsOnTable) */
case class Row(values : List[String])
/* rows in a table */
case class Rows(rows : List[Row])
/* a text filter to use it in a where clause */
case class Filter(text : String)
/* when we click on a foreign key, this is the information we need to open the new table
Contains:
- all columns of the original table
- the foreign key
- the rows checked by the user in the original table
 */
case class FollowKey(columns : List[Field], key : ForeignKey, rows : List[Row])
/* the foreign keys involving a table, with the table */
case class ForeignKeysForTable(table : String, keys : ForeignKeys)
/* all the foreign keys for all tables in the database */
case class ForeignKeysForTableList(keys : List[ForeignKeysForTable])
/* the fields used to sort the rows resulting from a query (order by) */
case class OrderByField(field : Field, direction: OrderByDirection)
case class OrderByFields(fields : List[OrderByField])