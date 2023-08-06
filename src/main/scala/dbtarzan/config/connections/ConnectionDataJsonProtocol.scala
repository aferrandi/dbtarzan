package dbtarzan.config.connections

import dbtarzan.config.password.PasswordJsonProtocol
import spray.json._

object IdentifierDelimitersJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.db.IdentifierDelimiters
  implicit val identifierDelimitersFormat: RootJsonFormat[IdentifierDelimiters] = jsonFormat(IdentifierDelimiters.apply,
  	"start", 
  	"end"
  	)
}

object SchemaJsonProtocol extends DefaultJsonProtocol {
  import dbtarzan.db.SchemaName
  implicit object SchemaFormat extends JsonFormat[SchemaName] {
    def write(schema: SchemaName): JsString = JsString(schema.schema)

    def read(json: JsValue): SchemaName = json match {
      case JsString(key) => SchemaName(key)
      case _ => throw new MatchError("can only make a Schema from a json string")
    }
  }
}

object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import IdentifierDelimitersJsonProtocol._
import PasswordJsonProtocol._
import SchemaJsonProtocol._
  implicit val connectionDataFormat: RootJsonFormat[ConnectionData] = jsonFormat(ConnectionData.apply,
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
