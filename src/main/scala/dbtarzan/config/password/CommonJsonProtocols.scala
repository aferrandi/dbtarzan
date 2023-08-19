package dbtarzan.config.password

import grapple.json.{*, given}

given JsonInput[Password] with
  def read(json: JsonValue): Password = Password(json.as[String])

given JsonOutput[Password] with
  def write(u: Password): JsonValue = JsonString(u.key)
