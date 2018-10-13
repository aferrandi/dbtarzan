package dbtarzan.db

import org.scalatest.FlatSpec
import dbtarzan.db.actor.DatabaseWorkerCache


class DatabaseWorkerCacheTest extends FlatSpec {
  "the cache" should "extract the primary key for a table only the first time it gets called" in {
    val cache = new DatabaseWorkerCache()
    cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key1", List("lastName")))))
    val keys = cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key2", List("lastName")))))
	assert("key1" === keys.keys.head.keyName)
  }

 "the cache" should "contain the columns " in {
    val cache = new DatabaseWorkerCache()
    val fields = cache.cachedFields("user", new Fields(List(Field("lastName", FieldType.STRING))))
	assert("lastName" === fields.fields.head.name)
  }

}

/*
    def cachedPrimaryKeys(tableName : String, extract : => PrimaryKeys) : PrimaryKeys = 
        primaryKeys.getOrElseUpdate(tableName, extract)
    def cachedColumns(tableName : String, extract : => Fields) : Fields = 
        columns.getOrElseUpdate(tableName, extract)
    def cachedForeignKeys(tableName : String, extract : => ForeignKeys) : ForeignKeys = 
        foreignKeys.getOrElseUpdate(tableName, extract)
*/