package dbtarzan.config.connections


import spray.json._

object IdentifierDelimitersJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.db.IdentifierDelimiters
  implicit val identifierDelimitersFormat = jsonFormat(IdentifierDelimiters, 
  	"start", 
  	"end"
  	)
}


object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import IdentifierDelimitersJsonProtocol._
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
		"catalog"
  	)
}
