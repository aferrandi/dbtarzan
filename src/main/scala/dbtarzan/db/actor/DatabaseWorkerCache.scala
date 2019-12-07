package dbtarzan.db.actor

import dbtarzan.db.{ PrimaryKeys, Fields, ForeignKeys }

import scala.collection.mutable.HashMap

/* 
    a single table is opened normally several times in DBTarzan, with different queries. This cache prevents DBTarzan to re-request columns, 
    primary keys and foreign keys each time the table is open.
    Don't confuse this with  DatabseWorker.foreignKeysFromFile, which contains the foreign keys read from a file 
*/
class DatabaseWorkerCache {
    private val primaryKeys = HashMap.empty[String, PrimaryKeys]
    private val fields = HashMap.empty[String, Fields]
    private val foreignKeys = HashMap.empty[String, ForeignKeys]

    def cachedPrimaryKeys(tableName : String, extract : => PrimaryKeys) : PrimaryKeys = 
        primaryKeys.getOrElseUpdate(tableName, extract)
    def cachedFields(tableName : String, extract : => Fields) : Fields = 
        fields.getOrElseUpdate(tableName, extract)
    def cachedForeignKeys(tableName : String, extract : => ForeignKeys) : ForeignKeys = 
        foreignKeys.getOrElseUpdate(tableName, extract)
}
