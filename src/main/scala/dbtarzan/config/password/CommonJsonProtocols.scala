package dbtarzan.config.password

import spray.json._

object PasswordJsonProtocol extends DefaultJsonProtocol {
  implicit object PasswordFormat extends JsonFormat[Password] {
    def write(password: Password): JsString = JsString(password.key)
    def read(json: JsValue): Password = json match {
      case JsString(key) => Password(key)
    }
  }
}