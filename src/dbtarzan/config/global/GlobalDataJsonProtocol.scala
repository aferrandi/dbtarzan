package dbtarzan.config.global


import spray.json._

object LanguageJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.localization.Language
  implicit val languageFormat = jsonFormat(Language, "language")
}

object PasswordJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.config.Password
  implicit val passwordFormat = jsonFormat(Password, "password")
}

object VerificationKeyJsonProtocol extends DefaultJsonProtocol {
  import PasswordJsonProtocol._
import dbtarzan.config.VerificationKey
  implicit val verificationKeyFormat = jsonFormat(VerificationKey, "verificationKey")
}

object GlobalDataJsonProtocol extends DefaultJsonProtocol {
  import LanguageJsonProtocol._
  import VerificationKeyJsonProtocol._
  implicit val globalDataFormat = jsonFormat(GlobalData, 
  	"language",
    "verificationKey"
  	)
}
