package dbtarzan.config.global

import org.scalatest.FlatSpec
import spray.json._

import dbtarzan.localization.Languages
import dbtarzan.config.{ Password, VerificationKey }


class GlobalDataReaderTest extends FlatSpec {
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
