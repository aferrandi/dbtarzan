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
    if(encryptionData.isDefined) {
      add(lblOriginalEncryptionKey, 0, 1)
      add(pwdOriginalEncryptionKey, 1, 1)
      add(lblNewEncryptionKey1, 0, 2)
      add(pwdNewEncryptionKey1, 1, 2)
      add(lblNewEncryptionKey2, 0, 3)
      add(pwdNewEncryptionKey2, 1, 3)      
    } else {
      add(lblNewEncryptionKey1, 0, 2)
      add(pwdNewEncryptionKey1, 1, 2)
      add(lblNewEncryptionKey2, 0, 3)
      add(pwdNewEncryptionKey2, 1, 3)      
    }

    padding = Insets(10)
    vgap = 10
    hgap = 10
  }
  show()

  def show() : Unit = {
      val isEncryptionKey = encryptionData.isDefined
      chkEncryptionKey.selected = isEncryptionKey 
      lblOriginalEncryptionKey.visible = isEncryptionKey
      pwdOriginalEncryptionKey.visible = isEncryptionKey
      lblNewEncryptionKey1.visible = isEncryptionKey 
      pwdNewEncryptionKey1.visible = isEncryptionKey 
      lblNewEncryptionKey2.visible = isEncryptionKey 
      pwdNewEncryptionKey2.visible = isEncryptionKey 
  }

  private def changeVisibility(visible : Boolean) : Unit = {
      lblOriginalEncryptionKey.visible = visible
      pwdOriginalEncryptionKey.visible = visible
      lblNewEncryptionKey1.visible = visible 
      pwdNewEncryptionKey1.visible = visible 
      lblNewEncryptionKey2.visible = visible 
      pwdNewEncryptionKey2.visible = visible 
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

