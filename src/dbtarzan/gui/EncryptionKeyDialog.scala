package dbtarzan.gui

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.stage.Stage
import scalafx.scene.control._
import scalafx.scene.layout.GridPane

import dbtarzan.config.EncryptionKey
import dbtarzan.localization.Localization

class EncryptionKeyDialog(stage : Stage, localization: Localization)  {
  def showDialog(): Option[EncryptionKey] = {
    val dialog = new Dialog[EncryptionKey]() {
      initOwner(stage)
      title = localization.encryptionKey
      // graphic = new ImageView(this.getClass.getResource("login_icon.png").toString)
    }
    val btnTypeEnter = new ButtonType("Login", ButtonData.OKDone)
    dialog.dialogPane().buttonTypes = Seq(btnTypeEnter, ButtonType.Cancel)
    val pwdEncryptionKey = new PasswordField() {
      promptText = localization.encryptionKey
    }

    val grid = new GridPane() {
      hgap = 10
      vgap = 10
      padding = Insets(20, 100, 10, 10)

      add(new Label(localization.encryptionKey+":"), 0, 1)
      add(pwdEncryptionKey, 1, 1)
    }

    val btnEnter = dialog.dialogPane().lookupButton(btnTypeEnter)
    btnEnter.disable = true
    pwdEncryptionKey.text.onChange { (_, _, newValue) => btnEnter.disable = newValue.trim().isEmpty}
    dialog.dialogPane().content = grid
    Platform.runLater(pwdEncryptionKey.requestFocus())
    dialog.resultConverter = dialogButton =>
      if (dialogButton == btnTypeEnter) EncryptionKey(pwdEncryptionKey.text())
      else null

    val result = dialog.showAndWait()
    result match {
      case Some(e)   => Some(EncryptionKey("pippo"))
      case None => None
    }
  }
}
