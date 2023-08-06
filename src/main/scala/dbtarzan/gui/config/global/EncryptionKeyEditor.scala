package dbtarzan.gui.config.global

import dbtarzan.config.connections.EncryptionKeyChange
import dbtarzan.config.global.EncryptionData
import dbtarzan.config.password.{EncryptionKey, EncryptionVerification, VerificationKey}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.geometry.Insets
import scalafx.scene.Parent
import scalafx.scene.control.{CheckBox, Label, PasswordField}
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority}


case class EncryptionKeyEditorData(
  newEncryptionData : Option[EncryptionData],
  change : EncryptionKeyChange
)

/* The list of database to choose from */
class EncryptionKeyEditor(
    encryptionData : Option[EncryptionData],
    localization: Localization
    ) extends TControlBuilder {
  private val chkEncryptionKey = new CheckBox {
    text = localization.changeEncryptionKey+": "+EncryptionVerification.possibleEncryptionKeyLength.map(_.toString).mkString(",")
    selected.onChange((_, _, newValue) => changeVisibility(newValue))
  }

  private val lblOriginalEncryptionKey = new Label { text = localization.originalEncryptionKey+":" }
  private val pwdOriginalEncryptionKey = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
  }

  private val lblNewEncryptionKey1 = new Label { text = localization.newEncryptionKey1+":" }
  private val pwdNewEncryptionKey1 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
  }

  private val lblNewEncryptionKey2 = new Label { text = localization.newEncryptionKey2+":" }
  private val pwdNewEncryptionKey2 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
  }

  private var passwordTextChanged = false

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.Always
      })
    add(chkEncryptionKey, 0, 0, 2, 1)
    addRow(1, lblOriginalEncryptionKey, pwdOriginalEncryptionKey)
    addRow(2, lblNewEncryptionKey1, pwdNewEncryptionKey1)
    addRow(3, lblNewEncryptionKey2, pwdNewEncryptionKey2)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }
  show()

  def show() : Unit = {
      val isEncryptionKey = encryptionData.isDefined
      chkEncryptionKey.selected = isEncryptionKey 
      JFXUtil.changeControlsVisibility(isEncryptionKey,
        lblOriginalEncryptionKey, 
        pwdOriginalEncryptionKey,
        lblNewEncryptionKey1,
        pwdNewEncryptionKey1,
        lblNewEncryptionKey2,
        pwdNewEncryptionKey2
      )
  }

  private def changeVisibility(visible : Boolean) : Unit = {
      val isEncryptionKey = encryptionData.isDefined
      JFXUtil.changeControlsVisibility(visible && isEncryptionKey,
        lblOriginalEncryptionKey, 
        pwdOriginalEncryptionKey
      )
      JFXUtil.changeControlsVisibility(visible,
        lblNewEncryptionKey1,
        pwdNewEncryptionKey1,
        lblNewEncryptionKey2,
        pwdNewEncryptionKey2
      )
  }

  private def isSamePassword() : Boolean = 
    newEncryptionKey1().equals(newEncryptionKey2())

  private def newEncryptionKey1() = EncryptionKey(pwdNewEncryptionKey1.text())

  private def newEncryptionKey2() = EncryptionKey(pwdNewEncryptionKey2.text())

  private def originalEncryptionKey() = EncryptionKey(pwdOriginalEncryptionKey.text())

  def canSave() : Boolean = {
    def originalEncryptionKeyVerified() : Boolean = 
      encryptionData.map(ed => EncryptionVerification.verify(originalEncryptionKey(), ed.verificationKey)).getOrElse(true)

    if(!chkEncryptionKey.selected()) 
      true
    else if(passwordTextChanged) {
      if(!originalEncryptionKeyVerified()) {
        JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings, localization.errorWrongEncryptionKey)
        false
      } else if(!isSamePassword()) {
        JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings, localization.errorEncryptionKeysDifferent)
        false
      } else if(!EncryptionVerification.isEncryptionKeyOfValidSize(newEncryptionKey1())) {
        JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings, localization.errorWrongEncryptionKeySize+":"+EncryptionVerification.possibleEncryptionKeyLength.map(_.toString).mkString(","))
        false
      } else
        true
    } else
      true
  }  

  private def calcVerificationKey() : Option[VerificationKey] = 
    Option(newEncryptionKey1()).map(EncryptionVerification.toVerification)

  def toData() : EncryptionKeyEditorData = 
    if(chkEncryptionKey.selected() && passwordTextChanged)
      EncryptionKeyEditorData(
        calcVerificationKey().map(k => EncryptionData(k)),
        EncryptionKeyChange(
          encryptionData.map(_ => originalEncryptionKey()),
          Some(newEncryptionKey1())
        )  
      )
    else
      EncryptionKeyEditorData(encryptionData, EncryptionKeyChange(None, None))
  

  def control : Parent = grid
}

