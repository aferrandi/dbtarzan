package dbtarzan.config.global

import dbtarzan.config.password.VerificationKey
import dbtarzan.db.CompositeId
import dbtarzan.localization.Language
import dbtarzan.config.password.{*, given}
import grapple.json.{*, given}
given JsonInput[Language] with
  def read(json: JsonValue): Language = Language(json("language"))

given JsonOutput[Language] with
  def write(u: Language): JsonObject = Json.obj("language" -> u.language)

given JsonInput[VerificationKey] with
  def read(json: JsonValue): VerificationKey = VerificationKey(json("password"))

given JsonOutput[VerificationKey] with
  def write(u: VerificationKey): JsonObject = Json.obj("password" -> u.password)

given JsonInput[EncryptionData] with
  def read(json: JsonValue): EncryptionData = EncryptionData(json("verificationKey"))

given JsonOutput[EncryptionData] with
  def write(u: EncryptionData): JsonObject = Json.obj("verificationKey" -> u.verificationKey)

given JsonInput[GlobalData] with
  def read(json: JsonValue): GlobalData = GlobalData(json("language"), json.readOption[EncryptionData]("encryptionData"))

given JsonOutput[GlobalData] with
  def write(u: GlobalData): JsonObject = Json.obj("language" -> u.language, "encryptionData" -> u.encryptionData)