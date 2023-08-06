package dbtarzan.config.composite

import dbtarzan.db.{Composite, CompositeId, SimpleDatabaseId}
import spray.json._

object CompositeIdJsonProtocol extends DefaultJsonProtocol {
  implicit val compositeIdFormat: RootJsonFormat[CompositeId] = jsonFormat(CompositeId.apply,
    "compositeName"
  )
}

object SimpleDatabaseIdJsonProtocol extends DefaultJsonProtocol {
  implicit val simpleDatabaseIdFormat: RootJsonFormat[SimpleDatabaseId] = jsonFormat(SimpleDatabaseId.apply,
    "databaseName"
  )
}

object CompositeJsonProtocol extends DefaultJsonProtocol {
  import CompositeIdJsonProtocol._
  import SimpleDatabaseIdJsonProtocol._
  implicit val compositeFormat: RootJsonFormat[Composite] = jsonFormat(Composite.apply,
  	"compositeId",
    "databaseIds",
  	)
}
