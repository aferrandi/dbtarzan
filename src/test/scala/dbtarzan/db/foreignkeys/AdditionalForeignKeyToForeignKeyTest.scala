package dbtarzan.db.foreignkeys

import dbtarzan.db.{AdditionalForeignKey, FieldsOnTable, ForeignKey, ForeignKeyDirection, ForeignKeys}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class AdditionalForeignKeyToForeignKeyTest extends AnyFlatSpec {
  "building foreign key query with delimiters" should "give a query with delimiters" in {
    val foreignKeys = AdditionalForeignKeyToForeignKey.toForeignKeys(
      List(AdditionalForeignKey("testKey", FieldsOnTable("user", List("userId")), FieldsOnTable("job", List("workerId"))))
    )
    assert(foreignKeys.size === 2)
    foreignKeys should contain ("user" -> ForeignKeys(List(ForeignKey("testKey_straight", FieldsOnTable("user", List("userId")), FieldsOnTable("job", List("workerId")), ForeignKeyDirection.STRAIGHT))))
    foreignKeys should contain ("job" -> ForeignKeys(List(ForeignKey("testKey_turned", FieldsOnTable("job", List("workerId")), FieldsOnTable("user", List("userId")), ForeignKeyDirection.TURNED))))
  }

}
