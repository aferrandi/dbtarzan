package dbtarzan.config.global

import dbtarzan.localization.Languages
import dbtarzan.config.password.{ Password, VerificationKey }
import org.scalatest.flatspec.AnyFlatSpec
import grapple.json.{ *, given }

class GlobalDataReaderTest extends AnyFlatSpec {
  "A global configuration" should "be parseable" in {
    val data = GlobalData(
            Languages.ITALIAN,
            Some(EncryptionData(VerificationKey(Password("amp1V30NtnMEyaIRciBh"))))
      )
    val json = Json.toPrettyPrint(Json.toJson(data))
    println(json)
    val extracted = GlobalDataReader.parseText(json)
    assert(extracted === data)
  }

  "A global configuration" should "be parseable also with no password" in {
    val data = GlobalData(
      Languages.ITALIAN,
      None
    )
    val json = Json.toPrettyPrint(Json.toJson(data))
    println(json)
    val extracted = GlobalDataReader.parseText(json)
    assert(extracted === data)
  }
}
