package dbtarzan.gui.browsingtable

import dbtarzan.db.{FieldsOnTable, ForeignKey, ForeignKeyDirection}
import dbtarzan.gui.util.TableIdLabel

object ForeignKeyText {

  /** the text shows the table name, the direction ("<"" if the foreign key is straight, ">"" if it is turned)
   * if there is more than one foreign key with the same "to" table, also the foreign key fields are displayed */
  def buildText(key : ForeignKeyWithSharingCheck): String = {
    val direction = directionText(key.key.direction)
    val fields = fieldsIfSharesTable(key).map(t => " " + t).getOrElse("")
    val table = TableIdLabel.toLabel(key.key.to.table)
    s"$direction $table$fields"
  }

  /** the tooltip show the whole foreign key */
  def buildTooltip(key: ForeignKey): String = {
    s"${key.name}\n- ${buildSide(key.from)}\n- ${buildSide(key.to)}"
  }

  private def buildSide(fields: FieldsOnTable): String =
    TableIdLabel.toLabel(fields.table) + fieldsToText(fields.fields)

  private def fieldsIfSharesTable(key : ForeignKeyWithSharingCheck): Option[String] =
    Some(key).filter(_.sharesToTable).map(k => fieldsToText(k.key.from.fields))

  private def fieldsToText(fields: List[String]): String = fields.mkString("(", ",", ")")

  private def directionText(direction: ForeignKeyDirection) = direction match {
    case ForeignKeyDirection.STRAIGHT => ">"
    case ForeignKeyDirection.TURNED => "<"
  }
}
