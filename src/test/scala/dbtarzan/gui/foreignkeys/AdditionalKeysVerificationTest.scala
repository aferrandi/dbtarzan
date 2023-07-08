package dbtarzan.gui.foreignkeys


import dbtarzan.db.{AdditionalForeignKey, FieldsOnTable, TableId}
import org.scalatest.flatspec.AnyFlatSpec
import dbtarzan.testutil.TestDatabaseIds

class AdditionalKeysVerificationTest extends AnyFlatSpec {

  "checking correct additional foreign keys" should "should succeed" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name1", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom1"), List("columnFrom1")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo1"), List("columnTo1"))),
        AdditionalForeignKey("name2", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom2"), List("columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo2"), List("columnTo2")))
    ))
//    assert(vs.correct === true)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with empty name" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(true, false, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with name <NEW>" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("<NEW>", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, true, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with no from columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List.empty), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty, List.empty))

  }

  "checking an additional foreign key with no to columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List.empty))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with same columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List("name"), List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with different columns number" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom1", "columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo1")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List("name"), List.empty, List.empty))
  }

  "checking an additional foreign keys with name duplications" should "should fail" in {

  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom1"), List("columnFrom1")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo1"), List("columnTo1"))),
        AdditionalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom2"), List("columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo2"), List("columnTo2")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List("name"), List.empty))
  }

  "checking an additional foreign keys with relations duplications" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name1", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo"))),
        AdditionalForeignKey("name2", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List.empty, List("name1")))
  }

}

