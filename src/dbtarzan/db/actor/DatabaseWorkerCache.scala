package dbtarzan.db.actor

import dbtarzan.db.{ PrimaryKeys, Fields, ForeignKeys }

import scala.collection.mutable.HashMap

class DatabaseWorkerCache {
    private val primaryKeys = HashMap.empty[String, PrimaryKeys]
    private val columns = HashMap.empty[String, Fields]
    private val foreignKeys = HashMap.empty[String, ForeignKeys]

    def cachedPrimaryKeys(tableName : String, extract : => PrimaryKeys) : PrimaryKeys = 
        primaryKeys.getOrElseUpdate(tableName, extract)
    def cachedColumns(tableName : String, extract : => Fields) : Fields = 
        columns.getOrElseUpdate(tableName, extract)
    def cachedForeignKeys(tableName : String, extract : => ForeignKeys) : ForeignKeys = 
        foreignKeys.getOrElseUpdate(tableName, extract)
}
