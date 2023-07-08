package dbtarzan.db.foreignkeys

import dbtarzan.db._
import dbtarzan.testutil.TestDatabaseIds
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.contain
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class AdditionalForeignKeyToForeignKeyTest extends AnyFlatSpec {
  "building foreign key query with delimiters" should "give a query with delimiters" in {
    val userTableId = TestDatabaseIds.simpleTableId( "user")
    val jobTableId = TestDatabaseIds.simpleTableId( "job")
    val foreignKeys = AdditionalForeignKeyToForeignKey.toForeignKeys(
      List(AdditionalForeignKey("testKey", FieldsOnTable(userTableId, List("userId")), FieldsOnTable(jobTableId, List("workerId"))))
    )
    assert(foreignKeys.size === 2)
    foreignKeys should contain (userTableId -> ForeignKeys(List(ForeignKey("testKey_straight", FieldsOnTable(userTableId, List("userId")), FieldsOnTable(jobTableId, List("workerId")), ForeignKeyDirection.STRAIGHT))))
    foreignKeys should contain (jobTableId -> ForeignKeys(List(ForeignKey("testKey_turned", FieldsOnTable(jobTableId, List("workerId")), FieldsOnTable(userTableId, List("userId")), ForeignKeyDirection.TURNED))))
  }

}
