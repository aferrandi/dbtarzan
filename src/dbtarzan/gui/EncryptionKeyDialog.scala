package dbtarzan.gui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.stage.Stage
import scalafx.scene.control._
import scalafx.scene.layout.GridPane
import scalafx.event.ActionEvent
import scalafx.event.Event
import javafx.event.EventHandler

import dbtarzan.config.{ EncryptionKey, VerificationKey, EncryptionVerification }
import dbtarzan.localization.Localization

class EncryptionKeyDialog(stage : Stage, localization: Localization)  {
  class Content(verificationKey : VerificationKey) {
    val dialog = new Dialog[EncryptionKey]() {
      initOwner(stage)
      title = localization.encryptionKey
    }
    val btnTypeEnter = buildButtonTypeEnter()
    val btnEnter = buildButtonEnter()
    val pwdEncryptionKey = new PasswordField() {
      promptText = localization.encryptionKey
      text.onChange { (_, _, newValue) => btnEnter.disable = newValue.trim().isEmpty}
    }
    val lblError = new Label();
    val grid = buildGrid()
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
        add(lblError, 0, 2, 2, 1)
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
          lblError.text = "Wrong master password length (must be long)"
          event.consume()
        } else if(!EncryptionVerification.verify(encryptionKey, verificationKey)) {
          lblError.text = "Wrong master password"
          event.consume()
        }
      })
      btnEnter.disable = true
      btnEnter
    }
    
    def showAndWait() : Option[EncryptionKey] = {
      val res = dialog.showAndWait((x: EncryptionKey) => x).asInstanceOf[Option[EncryptionKey]] 
      println("showAndWait")
      res
    }
      
  }

  def showDialog(verificationKey : VerificationKey): Option[EncryptionKey] = {
    val content = new Content(verificationKey) 
    content.showAndWait()
  }
}
