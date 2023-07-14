package dbtarzan.db.actor

import dbtarzan.db._
import org.scalatest.flatspec.AnyFlatSpec
import dbtarzan.testutil.TestDatabaseIds


class DatabaseWorkerCacheTest extends AnyFlatSpec {
  "the cache" should "extract the primary key for a table only the first time it gets called" in {
    val cache = new DatabaseWorkerCache()
    cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key1", List("lastName")))))
    val keys = cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key2", List("lastName")))))
	assert("key1" === keys.keys.head.keyName)
  }

 "the cache" should "contain the columns" in {
    val cache = new DatabaseWorkerCache()
    val fields = cache.cachedFields("user", new Fields(List(Field("lastName", FieldType.STRING, ""))))
	assert("lastName" === fields.fields.head.name)
  }

 "the cache" should "contain the foreign keys" in {
    val cache = new DatabaseWorkerCache()
    val userTableId = TestDatabaseIds.simpleTableId("user")
    val classTableId = TestDatabaseIds.simpleTableId("class")
    val keys = cache.cachedForeignKeys(userTableId, ForeignKeys(List(
      ForeignKey("lastNameKey", FieldsOnTable(userTableId, List("lastName")), FieldsOnTable(classTableId, List("lastName")), ForeignKeyDirection.STRAIGHT)
      )))
	assert("lastNameKey" === keys.keys.head.name)
  }
 "the cache" should "have different keys for different tables" in {
    val cache = new DatabaseWorkerCache()
    cache.cachedPrimaryKeys("user", new PrimaryKeys(List(PrimaryKey("key1", List("lastName")))))
    cache.cachedPrimaryKeys("city", new PrimaryKeys(List(PrimaryKey("key2", List("name")))))
    val keysUser = cache.cachedPrimaryKeys("user", new PrimaryKeys(List.empty))
    val keysCity = cache.cachedPrimaryKeys("city", new PrimaryKeys(List.empty))
	assert("key1" === keysUser.keys.head.keyName)
	assert("key2" === keysCity.keys.head.keyName)
  }
}
