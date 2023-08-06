package dbtarzan.db

object DBEnumsText {
    def fieldTypeToText(fieldType : FieldType): String =
      fieldType match {
          case FieldType.STRING => "STRING"
          case FieldType.INT => "INT"
          case FieldType.FLOAT => "FLOAT"
      }

    def orderByDirectionToText(direction : OrderByDirection): String =
      direction match {
          case OrderByDirection.ASC => "ASC"
          case OrderByDirection.DESC => "DESC"
      }

    def foreignKeyDirectionToText(direction : ForeignKeyDirection): String =
      direction match {
          case ForeignKeyDirection.STRAIGHT => "STRAIGHT"
          case ForeignKeyDirection.TURNED => "TURNED"
      }
}
