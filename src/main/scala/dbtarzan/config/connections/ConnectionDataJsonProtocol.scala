package dbtarzan.config.connections

import dbtarzan.config.password.PasswordJsonProtocol
import spray.json._

object IdentifierDelimitersJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.db.IdentifierDelimiters
  implicit val identifierDelimitersFormat = jsonFormat(IdentifierDelimiters, 
  	"start", 
  	"end"
  	)
}

object SchemaJsonProtocol extends DefaultJsonProtocol {
  import dbtarzan.db.SchemaName
  implicit object SchemaFormat extends JsonFormat[SchemaName] {
    def write(schema: SchemaName) = JsString(schema.schema)

    def read(json: JsValue): SchemaName = json match {
      case JsString(key) => SchemaName(key)
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
    "maxFieldSize",
		"catalog"
  	)
}
