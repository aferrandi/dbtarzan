package dbtarzan.gui.login

import dbtarzan.config.password.Password
import dbtarzan.db.{LoginPasswords, SimpleDatabaseId}
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.Includes.*
import scalafx.scene.control.{ButtonType, Dialog, Label, PasswordField}
import scalafx.scene.layout.GridPane
object PasswordDialog {
  def show(localization: Localization, simpleDatabaseIds: List[SimpleDatabaseId]): Option[LoginPasswords] = {
    val passwordFields = mkPasswordFields(simpleDatabaseIds.size)
    val titles = mkTitles(simpleDatabaseIds, localization)
    new Dialog[LoginPasswords]() {
      title = s"${if(simpleDatabaseIds.size > 1) localization.passwords else localization.password}"
      dialogPane().content = buildContent(passwordFields, titles)
      dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
      resultConverter = dialogButton => convertResult(simpleDatabaseIds, passwordFields, dialogButton)
    }.showAndWait().asInstanceOf[Option[LoginPasswords]]
  }

  private def buildContent(passwordFields: List[PasswordField], titles: List[Label]): GridPane = {
    val pane: GridPane = new GridPane {}
    titles.zipWithIndex.foreach((title, row) => pane.add(title, 0, row))
    passwordFields.zipWithIndex.foreach((passwordField, row) => pane.add(passwordField, 1, row))
    pane
  }

  private def mkTitles(simpleDatabaseIds: List[SimpleDatabaseId], localization: Localization): List[Label] = {
    simpleDatabaseIds.map(id => JFXUtil.buildTitle(s"${localization.database} ${id.databaseName}"))
  }

  private def mkPasswordFields(size: Int) : List[PasswordField] =
    List.range(0, size).map(_ => new PasswordField {
      text = ""
    })

  private def convertResult(simpleDatabaseIds: List[SimpleDatabaseId], passwordFields: List[PasswordField], dialogButton: ButtonType): LoginPasswords = {
    if (dialogButton == ButtonType.OK)
      LoginPasswords(simpleDatabaseIds.zip(passwordFields.map((field : PasswordField) => Password(field.text()))).toMap)
    else
      null
  }
}
