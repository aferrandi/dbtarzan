package dbtarzan.db.actor

import org.scalatest.FlatSpec
import dbtarzan.db.actor.AdditionalForeignKeysIntersection

class AdditionalForeignKeysIntersectionTest extends FlatSpec {
  /*
  "the cache" should "extract the primary key for a table only the first time it gets called" in {
    val foreignKeysByTable = Map(
        "Artist" -> ForeignKeys(List(
            ForeignKey("key1",  FieldsOnTable(table : String, fields : List[String]), to: FieldsOnTable, direction : ForeignKeyDirection)


    AdditionalForeignKeysIntersection.intersection(foreignKeysForCache: scala.collection.Map[String, ForeignKeys], additionalKeys :List[AdditionalForeignKey]) : List[String]

    val cache = new DatabaseWorkerCache()
    cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key1", List("lastName")))))
    val keys = cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key2", List("lastName")))))
	assert("key1" === keys.keys.head.keyName)

  }
    */
}