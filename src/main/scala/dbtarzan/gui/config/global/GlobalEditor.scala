package dbtarzan.gui.config.global

import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.control.{ ComboBox, ListCell, Label }
import scalafx.geometry.Insets
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.Includes._
import org.apache.pekko.actor.ActorRef

import dbtarzan.messages.Logger
import dbtarzan.config.global.GlobalData
import dbtarzan.gui.util.JFXUtil
import dbtarzan.config.connections.EncryptionKeyChange
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.{ Languages, Language, Localization }

/**
  table + constraint input box + foreign keys
*/
class GlobalEditor(
    data : GlobalData,
    localization: Localization,
    guiActor : ActorRef
  ) extends TControlBuilder {
  private val log = new Logger(guiActor)
  private val languages: scalafx.collections.ObservableBuffer[Language] = ObservableBuffer.from[Language](Languages.languages)

  private val cmbLanguages = new ComboBox[Language] {
    items = languages
    editable = false
    value = data.language
    cellFactory = (cell, value) => cell.text = value.language
    buttonCell = buildLanguageCell()
  }

  val encryptionEditor = new EncryptionKeyEditor(data.encryptionData, localization)

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.Always
      })
    add(new Label { text = localization.language+":" }, 0, 0)
    add(cmbLanguages, 1, 0)
    add(encryptionEditor.control, 0, 1, 2, 1)
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
  
  private def saveIfPossible(save : (GlobalData, EncryptionKeyChange)  => Unit) : Unit = 
    if(encryptionEditor.canSave()) {
      if(JFXUtil.areYouSure(localization.areYouSureSaveGlobalSettings, localization.saveGlobalSettings))    
        try { 
          val data = encryptionEditor.toData()
          save(
            GlobalData(
              cmbLanguages.getSelectionModel.selectedItem(),
              data.newEncryptionData
            ),
            data.change
          ) 
        } 
        catch {
          case ex : Exception => {
              JFXUtil.showErrorAlert(localization.errorSavingGlobalSettings+": ", ex.getMessage())
              log.error("Saving the global settings got", ex)
              ex.printStackTrace()
            }
        }
      }
  

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        cancel()
  }

  def onSave(save : (GlobalData, EncryptionKeyChange)  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def control : Parent = layout
}