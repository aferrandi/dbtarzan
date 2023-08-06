package dbtarzan.config.global

import spray.json._

import dbtarzan.localization.Languages
import dbtarzan.config.password.{ Password, VerificationKey }
import org.scalatest.flatspec.AnyFlatSpec


class GlobalDataReaderTest extends AnyFlatSpec {
  import GlobalDataJsonProtocol._

  "A global configuration" should "be parseable" in {
    val data = GlobalData(
            Languages.ITALIAN,
            Some(EncryptionData(VerificationKey(Password("amp1V30NtnMEyaIRciBh"))))
      )
    val json = data.toJson.prettyPrint
    println(json)
    val extracted = GlobalDataReader.parseText(json)
    assert(extracted === data)
  }
}
