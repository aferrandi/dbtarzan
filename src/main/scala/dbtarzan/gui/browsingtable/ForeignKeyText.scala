package dbtarzan.gui.browsingtable

import dbtarzan.db.{FieldsOnTable, ForeignKey, ForeignKeyDirection}
import dbtarzan.gui.util.TableIdLabel

object ForeignKeyText {

  /** the tooltip show the whole foreign key */
  def buildTooltip(key: ForeignKey): String = {
    s"${key.name}\n- ${buildSide(key.from)}\n- ${buildSide(key.to)}"
  }

  private def buildSide(fields: FieldsOnTable): String =
    TableIdLabel.toLabel(fields.table) + fieldsToText(fields.fields)

  private def fieldsToText(fields: List[String]): String = fields.mkString("(", ",", ")")

  private def directionText(direction: ForeignKeyDirection) = direction match {
    case ForeignKeyDirection.STRAIGHT => ">"
    case ForeignKeyDirection.TURNED => "<"
  }
}
