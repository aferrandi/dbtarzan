package dbtarzan.gui.foreignkeys


import dbtarzan.db.{VirtualalForeignKey, FieldsOnTable}
import org.scalatest.flatspec.AnyFlatSpec
import dbtarzan.testutil.TestDatabaseIds

class VirtualKeysVerificationTest extends AnyFlatSpec {

  "checking correct virtual foreign keys" should "should succeed" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name1", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom1"), List("columnFrom1")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo1"), List("columnTo1"))),
        VirtualalForeignKey("name2", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom2"), List("columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo2"), List("columnTo2")))
    ))
//    assert(vs.correct === true)
    assert(vs === VirtualKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an virtual foreign key with empty name" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(true, false, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an virtual foreign key with name <NEW>" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("<NEW>", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, true, List.empty, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an virtual foreign key with no from columns" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List.empty), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty, List.empty))

  }

  "checking an virtual foreign key with no to columns" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List.empty))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty, List.empty))
  }

  "checking an virtual foreign key with same columns" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List.empty, List("name"), List.empty, List.empty, List.empty))
  }

  "checking an virtual foreign key with different columns number" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom1", "columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo1")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List.empty, List.empty, List("name"), List.empty, List.empty))
  }

  "checking an virtual foreign keys with name duplications" should "should fail" in {

    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom1"), List("columnFrom1")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo1"), List("columnTo1"))),
        VirtualalForeignKey("name", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom2"), List("columnFrom2")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo2"), List("columnTo2")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List("name"), List.empty))
  }

  "checking an virtual foreign keys with relations duplications" should "should fail" in {
    val vs = VirtualKeysVerification.verify(List(
        VirtualalForeignKey("name1", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo"))),
        VirtualalForeignKey("name2", FieldsOnTable(TestDatabaseIds.simpleTableId("tableFrom"), List("columnFrom")), FieldsOnTable(TestDatabaseIds.simpleTableId("tableTo"), List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === VirtualKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List.empty, List("name1")))
  }

}

