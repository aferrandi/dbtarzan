package dbtarzan.db.foreignkeys.files

import dbtarzan.db.VirtualalForeignKey
import grapple.json.{*, given}
object VirtualForeignKeysWriter {
  def toText(list: List[VirtualalForeignKey]): String = {
    Json.toPrettyPrint(Json.toJson(list))
  }
}
