package dbtarzan.gui.config.global

import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.control.{ ComboBox, ListCell, Label }
import scalafx.geometry.Insets
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import dbtarzan.config.global.GlobalData
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import scalafx.Includes._
import dbtarzan.localization.{ Languages, Language, Localization }

/**
  table + constraint input box + foreign keys
*/
class GlobalEditor(
    data : GlobalData,
    localization: Localization
  ) extends TControlBuilder {
  
  val languages = ObservableBuffer(Languages.languages)

  val cmbLanguages = new ComboBox[Language] {
    items = languages
    editable = false
    value = data.language
    cellFactory = { _ => buildLanguageCell() }
    buttonCell = buildLanguageCell()
  }

  val encryptionEditor = new EncryptionKeyEditor(data.verificationKey, localization)

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
      })
    add(new Label { text = localization.language+":" }, 0, 0)
    add(cmbLanguages, 1, 0)
    add(encryptionEditor.control, 0, 1)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  private val buttons = new GlobalButtons(localization) 

  private val layout = new BorderPane {
    center = grid
    bottom = buttons.control
  }

  private def buildLanguageCell() = new ListCell[Language] {
      item.onChange { 
        (_, _, value) => Option(value).foreach(v => text = v.language)
        }
  }
  
  private def saveIfPossible(save : GlobalData  => Unit) : Unit = {
    if(!encryptionEditor.cannotSave()) {
      if(JFXUtil.areYouSure(localization.areYouSureSaveGlobalSettings, localization.saveGlobalSettings))    
        try { save(GlobalData(
          cmbLanguages.getSelectionModel().selectedItem(),
          encryptionEditor.toData()
        )) } 
        catch {
          case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings+": ", ex.getMessage())
        }
      } else
        JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings, localization.errorWrongEncryptionKey)
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        cancel()
  }

  def onSave(save : GlobalData  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def control : Parent = layout
}