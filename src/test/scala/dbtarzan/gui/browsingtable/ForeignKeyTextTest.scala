package dbtarzan.gui.browsingtable

import dbtarzan.db.*
import org.scalatest.flatspec.AnyFlatSpec


class ForeignKeyTextTest extends AnyFlatSpec {
  "If shared, the foreign key text" should "should contain the target table with the fields used to refer to it" in {
    val text: String = ForeignKeyText.buildText(ForeignKeyWithSharingCheck(buildForeignKey(), true))
    assert(text === "> table2 (cat,dog)")
  }

  "If not shared, the foreign key text" should "should only contain the target table" in {
    val text: String = ForeignKeyText.buildText(ForeignKeyWithSharingCheck(buildForeignKey(), false))
    assert(text === "> table2")
  }

  "If not shared, the foreign key tooltip" should "key name and description of the involved tables" in {
    val text: String = ForeignKeyText.buildTooltip(buildForeignKey())
    assert(text ===
"""giovanni
- simpledb1.table1(cat,dog)
- table2(bird,fish)"""
    )
  }

  private def buildForeignKey(): ForeignKey = {
    val fieldsOnTable1 = FieldsOnTable(TableId(DatabaseId(Right(CompositeId("composite1"))), SimpleDatabaseId("simpledb1"), tableName = "table1"), List("cat", "dog"))
    val fieldsOnTable2 = FieldsOnTable(TableId(DatabaseId(Left(SimpleDatabaseId("simpledb2"))), SimpleDatabaseId("simpledb2"), tableName = "table2"), List("bird", "fish"))
    ForeignKey("giovanni", fieldsOnTable1, fieldsOnTable2, ForeignKeyDirection.STRAIGHT)
  }
}