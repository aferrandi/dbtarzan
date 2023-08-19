package dbtarzan.config.global

import dbtarzan.config.password.VerificationKey
import dbtarzan.db.CompositeId
import dbtarzan.localization.Language
import dbtarzan.config.password.*
import grapple.json.{*, given}
given JsonInput[Language] with
  def read(json: JsonValue) = Language(json("language"))

given JsonOutput[Language] with
  def write(u: Language) = Json.obj("language" -> u.language)

given JsonInput[VerificationKey] with
  def read(json: JsonValue) = VerificationKey(PasswordJsonInput.read(json("password")))

given JsonOutput[VerificationKey] with
  def write(u: VerificationKey) = Json.obj("password" -> PasswordJsonOutput.write(u.password))

given JsonInput[EncryptionData] with
  def read(json: JsonValue) = EncryptionData(json("verificationKey"))

given JsonOutput[EncryptionData] with
  def write(u: EncryptionData) = Json.obj("verificationKey" -> u.verificationKey)

given JsonInput[GlobalData] with
  def read(json: JsonValue) = GlobalData(json("language"), json.map[EncryptionData]("encryptionData"))

given JsonOutput[GlobalData] with
  def write(u: GlobalData) = Json.obj("language" -> u.language, "encryptionData" -> u.encryptionData)