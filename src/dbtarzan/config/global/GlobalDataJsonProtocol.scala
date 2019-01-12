package dbtarzan.config.global


import spray.json._

object LanguageJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.localization.Language
  implicit val languageFormat = jsonFormat(Language, "language")
}

object GlobalDataJsonProtocol extends DefaultJsonProtocol {
  import LanguageJsonProtocol._
  implicit val globalDataFormat = jsonFormat(GlobalData, 
  	"language"
  	)
}
