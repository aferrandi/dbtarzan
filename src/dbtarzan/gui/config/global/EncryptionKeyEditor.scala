package dbtarzan.gui.config.global

import scalafx.scene.control.{ PasswordField, CheckBox, Label }
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.Parent
import scalafx.geometry.Insets
import scalafx.Includes._

import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.config.connections.EncryptionKeyChange
import dbtarzan.config.{ EncryptionKey, VerificationKey, EncryptionVerification }
import dbtarzan.config.global.EncryptionData
import dbtarzan.localization.Localization


case class EncryptionKeyEditorData(
  newEncryptionData : Option[EncryptionData],
  change : EncryptionKeyChange
)

/* The list of database to choose from */
class EncryptionKeyEditor(
    encryptionData : Option[EncryptionData],
    localization: Localization
    ) extends TControlBuilder {
  val chkEncryptionKey = new CheckBox {
    text = localization.changeEncryptionKey+": "+EncryptionVerification.possibleEncryptionKeyLength.map(_.toString).mkString(",")
    selected.onChange((_, _, newValue) => changeVisibility(newValue))
  }    

  val lblOriginalEncryptionKey = new Label { text = localization.originalEncryptionKey+":" }
  val pwdOriginalEncryptionKey = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
	}

  val lblNewEncryptionKey1 = new Label { text = localization.newEncryptionKey1+":" }
  val pwdNewEncryptionKey1 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
	}

  val lblNewEncryptionKey2 = new Label { text = localization.newEncryptionKey2+":" }
  val pwdNewEncryptionKey2 = new PasswordField {
    text.onChange { (_, _, _) => passwordTextChanged = true	}
  }

  var passwordTextChanged = false

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
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
    Option(newEncryptionKey1()).map(EncryptionVerification.toVerification(_))

  def toData() : EncryptionKeyEditorData = 
    if(chkEncryptionKey.selected())
      EncryptionKeyEditorData(
        calcVerificationKey().map(EncryptionData(_)), 
        EncryptionKeyChange(
          encryptionData.map(ed => originalEncryptionKey()),
           Some(newEncryptionKey1())
           )  
      )
    else
      EncryptionKeyEditorData(encryptionData, EncryptionKeyChange(None, None))
  

  def control : Parent = grid
}

