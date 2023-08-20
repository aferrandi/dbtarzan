package dbtarzan.db.foreignkeys.files

import dbtarzan.db.ForeignKeyDirection

case class FieldsOnTableOneDb(table : String, fields : List[String])
case class ForeignKeyOneDb(name: String, from : FieldsOnTableOneDb, to: FieldsOnTableOneDb, direction : ForeignKeyDirection)
case class ForeignKeysOneDb(keys : List[ForeignKeyOneDb])
case class ForeignKeysForTableOneDb(table : String, keys : ForeignKeysOneDb)
case class ForeignKeysForTableListOneDb(keys : List[ForeignKeysForTableOneDb])
