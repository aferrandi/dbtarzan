package dbtarzan.config


import spray.json._

object IdentifierDelimitersJsonProtocol extends DefaultJsonProtocol {
import dbtarzan.db.IdentifierDelimiters
import DefaultJsonProtocol._
  implicit val identifierDelimitersFormat = jsonFormat(IdentifierDelimiters, 
  	"start", 
  	"end"
  	)
}


object ConnectionDataJsonProtocol extends DefaultJsonProtocol {
import DefaultJsonProtocol._
import IdentifierDelimitersJsonProtocol._
  implicit val connectionDataFormat = jsonFormat(ConnectionData, 
  	"jar", 
  	"name", 
  	"driver", 
  	"url", 
  	"schema", 
  	"user", 
  	"password", 
  	"instances", 
  	"identifierDelimiters"
  	)
}
