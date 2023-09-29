package dbtarzan.gui.login

import dbtarzan.config.password.Password
import dbtarzan.db.{LoginPasswords, SimpleDatabaseId}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.scene.control.{ButtonType, Dialog, PasswordField}
import scalafx.Includes.*
import scalafx.scene.layout.{BorderPane, VBox}
object PasswordDialog {
  def show(localization: Localization, simpleDatabaseIds: List[SimpleDatabaseId]): Option[LoginPasswords] = {
    val passwordFields = mkPasswordFields(simpleDatabaseIds.size)
    val items = mkItems(localization, simpleDatabaseIds, passwordFields)
    new Dialog[LoginPasswords]() {
      title = s"${localization.password}"
      dialogPane().content = new VBox { children = items }
      dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      resultConverter = dialogButton => convertResult(simpleDatabaseIds, passwordFields, dialogButton)
    }.showAndWait().asInstanceOf[Option[LoginPasswords]]
  }

  private def mkPasswordFields(size: Int) : List[PasswordField] =
    List.range(0, size).map(_ => new PasswordField {
      text = ""
    })

  private def mkItems(localization: Localization, simpleDatabaseIds: List[SimpleDatabaseId], passwordFields: List[PasswordField]) =
    simpleDatabaseIds.zip(passwordFields).map(
      (id, passwordField) => JFXUtil.withLeftTitle(passwordField, s"${localization.databases} ${id.databaseName}")
    )

  private def convertResult(simpleDatabaseIds: List[SimpleDatabaseId], passwordFields: List[PasswordField], dialogButton: ButtonType): LoginPasswords = {
    if (dialogButton == ButtonType.OK)
      LoginPasswords(simpleDatabaseIds.zip(passwordFields.map((field : PasswordField) => Password(field.text()))).toMap)
    else
      null
  }
}
