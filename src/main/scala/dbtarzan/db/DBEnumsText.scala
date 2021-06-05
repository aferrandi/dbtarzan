package dbtarzan.db

object DBEnumsText {
    def fieldTypeToText(fieldType : FieldType): String =
      fieldType match {
          case FieldType.STRING => "STRING"
          case FieldType.INT => "INT"
          case FieldType.FLOAT => "FLOAT"
          case _ => "<ERROR>"
      }

    def orderByDirectionToText(direction : OrderByDirection): String =
      direction match {
          case OrderByDirection.ASC => "ASC"
          case OrderByDirection.DESC => "DESC"
          case _ => "<ERROR>"
      }

    def foreignKeyDirectionToText(direction : ForeignKeyDirection): String =
      direction match {
          case ForeignKeyDirection.STRAIGHT => "STRAIGHT"
          case ForeignKeyDirection.TURNED => "TURNED"
          case _ => "<ERROR>"
      }
}
