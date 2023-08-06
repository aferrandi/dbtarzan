package dbtarzan.config.global

import spray.json._

import dbtarzan.config.password.PasswordJsonProtocol

object LanguageJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.localization.Language
  implicit val languageFormat: RootJsonFormat[Language] = jsonFormat(Language.apply, "language")
}

object VerificationKeyJsonProtocol extends DefaultJsonProtocol {
  import PasswordJsonProtocol._
  import dbtarzan.config.password.VerificationKey
  implicit object VerificationKeyFormat extends JsonFormat[VerificationKey] {
    def write(verificationKey: VerificationKey): JsString = PasswordFormat.write(verificationKey.password)
    def read(json: JsValue): VerificationKey = VerificationKey(PasswordFormat.read(json))
  }
}

object EncryptionDataJsonProtocol extends DefaultJsonProtocol {
import VerificationKeyJsonProtocol._
  implicit val encryptionDataFormat: RootJsonFormat[EncryptionData] = jsonFormat(EncryptionData.apply, "verificationKey")
}

object GlobalDataJsonProtocol extends DefaultJsonProtocol {
  import LanguageJsonProtocol._
  import EncryptionDataJsonProtocol._
  implicit val globalDataFormat: RootJsonFormat[GlobalData] = jsonFormat(GlobalData.apply,
  	"language",
    "encryptionData"
  	)
}
