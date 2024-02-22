package dbtarzan.gui.main

import dbtarzan.config.password.{EncryptionKey, PasswordEncryption, VerificationKey}
import dbtarzan.localization.Localization
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.JFXApp3

class EncryptionKeyExtractor(verificationKey: Option[VerificationKey],
                             localization: Localization) {
  private var encryptionKey: Option[EncryptionKey] = None
  private val encryptionKeyDialog = new EncryptionKeyDialog(localization)

  def extractEncryptionKey(stage: JFXApp3.PrimaryStage): Option[EncryptionKey] = {
    if (encryptionKey.isEmpty)
      encryptionKey = verificationKey.map(
        vkey => encryptionKeyDialog.showDialog(stage, vkey)
      ).getOrElse(
        Some(PasswordEncryption.defaultEncryptionKey)
      )
    encryptionKey
  }
}
