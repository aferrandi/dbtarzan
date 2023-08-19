package dbtarzan.config.password

import dbtarzan.db.IdentifierDelimiters
import grapple.json.{*, given}

object PasswordJsonInput {
  def read(json: JsonValue): Password = Password(json.as[JsonString].value)
}

object PasswordJsonOutput {
  def write(u: Password): JsonObject = JsonString(u.key)
}

