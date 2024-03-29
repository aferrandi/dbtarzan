package dbtarzan.gui.main

import dbtarzan.config.password.{EncryptionKey, EncryptionVerification, VerificationKey}
import dbtarzan.localization.Localization
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.*
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.layout.GridPane
import scalafx.stage.Stage

class EncryptionKeyDialog(localization: Localization)  {
  class Content(stage : Stage, verificationKey : VerificationKey) {
    private val dialog = new Dialog[EncryptionKey]() {
      initOwner(stage)
      title = localization.encryptionKey
    }
    private val btnTypeEnter = buildButtonTypeEnter()
    private val btnEnter = buildButtonEnter()
    private val pwdEncryptionKey = new PasswordField() {
      promptText = localization.encryptionKey
      text.onChange { (_, _, newValue) => btnEnter.disable = newValue.trim().isEmpty}
    }
    private val lblError = new Label() {
      wrapText = true
      minWidth = 400
    }
    private val grid = buildGrid()
    dialog.dialogPane().content = grid
    Platform.runLater(pwdEncryptionKey.requestFocus())
    dialog.resultConverter = dialogButton =>
      if (dialogButton == btnTypeEnter)
        EncryptionKey(pwdEncryptionKey.text()) 
      else 
        null

    private def buildGrid() = new GridPane() {
        hgap = 10
        vgap = 10
        padding = Insets(20, 10, 10, 10)
        add(new Label(localization.encryptionKey+":"), 0, 1)
        add(pwdEncryptionKey, 1, 1)
        add(lblError, 0, 2, 2, 2)
    }

    private def buildButtonTypeEnter() : ButtonType = {
      val btnTypeEnter = new ButtonType("Login", ButtonData.OKDone)
      dialog.dialogPane().buttonTypes = Seq(btnTypeEnter, ButtonType.Cancel)
      btnTypeEnter
    }

    private def buildButtonEnter() : Button = {
      val btnEnter = dialog.dialogPane().lookupButton(btnTypeEnter).asInstanceOf[javafx.scene.control.Button]
      btnEnter.addEventFilter(javafx.event.ActionEvent.ACTION, (event:javafx.event.ActionEvent) => {
        val encryptionKey = EncryptionKey(pwdEncryptionKey.text())
        if(!EncryptionVerification.isEncryptionKeyOfValidSize(encryptionKey)) {
          lblError.text = localization.errorWrongEncryptionKeySize+EncryptionVerification.possibleEncryptionKeyLength.map(_.toString).mkString(",")
          event.consume()
        } else if(!EncryptionVerification.verify(encryptionKey, verificationKey)) {
          lblError.text = localization.errorWrongEncryptionKey
          event.consume()
        }
      })
      btnEnter.disable = true
      btnEnter
    }
    
    def showAndWait() : Option[EncryptionKey] = {
      val res = dialog.showAndWait((x: EncryptionKey) => x).asInstanceOf[Option[EncryptionKey]] 
      res
    }
  }

  def showDialog(stage : Stage, verificationKey : VerificationKey): Option[EncryptionKey] = {
    val content = new Content(stage, verificationKey)
    content.showAndWait()
  }
}
