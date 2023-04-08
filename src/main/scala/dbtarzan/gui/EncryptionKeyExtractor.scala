package dbtarzan.gui

import dbtarzan.config.password.{EncryptionKey, PasswordEncryption, VerificationKey}
import dbtarzan.localization.Localization
import scalafx.application.JFXApp.PrimaryStage

class EncryptionKeyExtractor(verificationKey: Option[VerificationKey],
                             localization: Localization) {
  private var encryptionKey: Option[EncryptionKey] = None
  private val encryptionKeyDialog = new EncryptionKeyDialog(localization)

  def extractEncryptionKey(stage: PrimaryStage): Option[EncryptionKey] = {
    if (encryptionKey.isEmpty)
      encryptionKey = verificationKey.map(
        vkey => encryptionKeyDialog.showDialog(stage, vkey)
      ).getOrElse(
        Some(PasswordEncryption.defaultEncryptionKey)
      )
    encryptionKey
  }
}
