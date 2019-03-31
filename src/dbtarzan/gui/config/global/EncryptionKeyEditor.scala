package dbtarzan.gui.config.global

import scalafx.scene.control.{ PasswordField, CheckBox }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.geometry.Insets
import scalafx.Includes._

import dbtarzan.gui.TControlBuilder
import dbtarzan.config.{ EncryptionKey, VerificationKey, EncryptionVerification }
import dbtarzan.localization.Localization

/* The list of database to choose from */
class EncryptionKeyEditor(
    verificationKey : Option[VerificationKey],
    localization: Localization
    ) extends TControlBuilder {
  val chkEncryptionKey = new CheckBox {
    text = localization.encryptionKey
    selected.onChange((_, _, newValue) => changeVisibility(newValue))
  }    
  val pwdEncryptionKey1 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
	}

  val pwdEncryptionKey2 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
  }

  var passwordTextChanged = false

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
      })
    add(chkEncryptionKey, 0, 0)
    add(pwdEncryptionKey1, 1, 0)
    add(pwdEncryptionKey2, 1, 1)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }
  show()

  def show() : Unit = {
      val isEncryptionKey = verificationKey.isDefined
      chkEncryptionKey.selected = isEncryptionKey 
      pwdEncryptionKey1.visible = isEncryptionKey 
      pwdEncryptionKey2.visible = isEncryptionKey 
  }

  private def changeVisibility(visible : Boolean) : Unit = {
      pwdEncryptionKey1.visible = visible 
      pwdEncryptionKey2.visible = visible 
  }

  private def isSamePassword() : Boolean = 
    pwdEncryptionKey1.text == pwdEncryptionKey2.text

  def cannotSave() : Boolean = 
    chkEncryptionKey.selected() && passwordTextChanged && !isSamePassword()

  def toData() : Option[VerificationKey] = 
    if(chkEncryptionKey.selected())
        Some(pwdEncryptionKey1.text()).map(EncryptionKey(_)).map(EncryptionVerification.toVerification(_))
    else
        verificationKey
  

  def control : Parent = grid
}

