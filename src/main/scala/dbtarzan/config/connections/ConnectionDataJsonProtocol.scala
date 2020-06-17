package dbtarzan.config.connections

import spray.json._

import dbtarzan.config.password.PasswordJsonProtocol

object IdentifierDelimitersJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.db.IdentifierDelimiters
  implicit val identifierDelimitersFormat = jsonFormat(IdentifierDelimiters, 
  	"start", 
  	"end"
  	)
}

object SchemaJsonProtocol extends DefaultJsonProtocol {
  import dbtarzan.db.Schema
  implicit object SchemaFormat extends JsonFormat[Schema] {
    def write(schema: Schema) = JsString(schema.name)
    def read(json: JsValue): Schema = json match {
      case JsString(key) => Schema(key)
    }
  }
}

object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import IdentifierDelimitersJsonProtocol._
import PasswordJsonProtocol._
import SchemaJsonProtocol._
  implicit val connectionDataFormat = jsonFormat(ConnectionData, 
  	"jar", 
  	"name", 
  	"driver", 
  	"url", 
  	"schema", 
  	"user", 
  	"password", 
    "passwordEncrypted", 
  	"instances", 
  	"identifierDelimiters",
    "maxRows",
		"queryTimeoutInSeconds",
		"catalog"
  	)
}
