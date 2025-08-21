package dbtarzan.db

import dbtarzan.config.password.Password
import dbtarzan.types.Binaries.Binary

type FieldValue = String | Int | Double | Binary

case class SimpleDatabaseId(databaseName : String)
/* The name given by the user to the composite identifies it */
case class CompositeId(compositeName: String)
/* A composite is a composition of multiple databases */
case class Composite(compositeId: CompositeId, databaseIds: List[SimpleDatabaseId], showAlsoIndividualDatabases: Boolean)
/* The name given by the user to the database identifies it */
case class DatabaseId(origin : Either[SimpleDatabaseId, CompositeId])
/* The database id + the table name identifies a table */
case class TableId(databaseId : DatabaseId, simpleDatabaseId: SimpleDatabaseId, tableName : String)
/* an sql expression */
case class QuerySql(sql: String)
/* a table: its name, the name of the original table if it comes from another table */
case class TableDescription(name: String, origin : Option[String], notes: Option[String])
/* the tables in a databases */
case class TableIds(tableIds : List[TableId])

case class TableNames(names : List[String])

/* a field in a table (name and type) */
case class Field(name : String,  fieldType : FieldType, typeDescription : String, maxLength: Option[Int])
/* all fields in a table */
case class Fields(fields : List[Field])
/* all fields in a table (with the table name)n*/
case class FieldsOnTable(table : TableId, fields : List[String])
/* a foreign key is a relation between two tables. It has a name and matches fields on the two tables (can clearly be more than one) */
case class ForeignKey(name: String, from : FieldsOnTable, to: FieldsOnTable, direction : ForeignKeyDirection)
/* the foreign keys involving a table */
case class ForeignKeys(keys : List[ForeignKey])
/* 
    an virtual foreign key does not have a direction (as a ForeignKey) becauee it gets actually resolved in a Foreign key (STRAIGHT)
    and the same key from the "to" table to the "from" table (TURNED)
*/
case class VirtualalForeignKey(name: String, from : FieldsOnTable, to: FieldsOnTable)
/* a fields with its content in a row */
case class FieldWithValue(field : String, value : FieldValue)
/* a row. The values are in the same order as in the table description (FieldsOnTable) */
case class Row(values : List[String|Int|Double|Binary])
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
case class FollowKey(columns : List[Field], key : ForeignKey, rows : Option[List[Row]])
/* the foreign keys involving a table, with the table */
case class ForeignKeysForTable(tableId : TableId, keys : ForeignKeys)
/* all the foreign keys for all tables in the database */
case class ForeignKeysForTableList(keys : List[ForeignKeysForTable])
/* the fields used to sort the rows resulting from a query (order by) */
case class OrderByField(field : Field, direction: OrderByDirection)
case class OrderByFields(fields : List[OrderByField])
/* the primary keys of a table */
case class PrimaryKey(keyName: String, fields : List[String])
case class PrimaryKeys(keys : List[PrimaryKey])
/* the schmas of a database */
case class SchemaName(schema : String)

case class SchemaNames(names: List[SchemaName])

case class SchemaId(databaseId: DatabaseId, simpleDatabaseId: SimpleDatabaseId, schema : SchemaName)
case class SchemaIds(schemaIds : List[SchemaId])
/* an index of a table in the database */
case class IndexField(name: String, direction: Option[OrderByDirection])
case class Index(name: String, fields: List[IndexField])
case class Indexes(indexes: List[Index])
case class SimpleDatabaseInfo(simpleDatabaseId: SimpleDatabaseId, needsPassword: Boolean)
case class CompositeInfo(compositeId: CompositeId, databaseInfos: List[SimpleDatabaseInfo])
case class DatabaseInfo(origin : Either[SimpleDatabaseInfo, CompositeInfo])
case class LoginPasswords(loginPasswords : Map[SimpleDatabaseId, Password])