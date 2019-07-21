package dbtarzan.gui.foreignkeys

import org.scalatest.FlatSpec

import dbtarzan.db.{ AdditionalForeignKey, FieldsOnTable }

class AdditionalKeysVerificationTest extends FlatSpec {
  "checking correct additional foreign keys" should "should succeed" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name1", FieldsOnTable("tableFrom1", List("columnFrom1")), FieldsOnTable("tableTo1", List("columnTo1"))),
        AdditionalForeignKey("name2", FieldsOnTable("tableFrom2", List("columnFrom2")), FieldsOnTable("tableTo2", List("columnTo2")))
    ))
//    assert(vs.correct === true)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with empty name" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableTo", List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(true, false, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with name <NEW>" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("<NEW>", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableTo", List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, true, List.empty, List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with no from columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable("tableFrom", List.empty), FieldsOnTable("tableTo", List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty))

  }

  "checking an additional foreign key with no to columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableTo", List.empty))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List("name"), List.empty, List.empty, List.empty))
  }

  "checking an additional foreign key with same columns" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableFrom", List("columnFrom")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List("name"), List.empty, List.empty))
  }

  "checking an additional foreign keys with name duplications" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name", FieldsOnTable("tableFrom1", List("columnFrom1")), FieldsOnTable("tableTo1", List("columnTo1"))),
        AdditionalForeignKey("name", FieldsOnTable("tableFrom2", List("columnFrom2")), FieldsOnTable("tableTo2", List("columnTo2")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List("name"), List.empty))
  }

  "checking an additional foreign keys with relations duplications" should "should fail" in {
  	val vs = AdditionalKeysVerification.verify(List(
        AdditionalForeignKey("name1", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableTo", List("columnTo"))),
        AdditionalForeignKey("name2", FieldsOnTable("tableFrom", List("columnFrom")), FieldsOnTable("tableTo", List("columnTo")))
    ))
    assert(vs.correct === false)
    assert(vs === AdditionalKeysVerificationResult(false, false, List.empty, List.empty, List.empty, List("name1")))
  }

}

