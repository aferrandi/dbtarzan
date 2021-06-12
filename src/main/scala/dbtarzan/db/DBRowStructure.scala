package dbtarzan.db

case class DBRowStructure(
  tableName: String,
  columns : Fields,
  filter : List[FieldWithValue],
  attributes : QueryAttributes
)
