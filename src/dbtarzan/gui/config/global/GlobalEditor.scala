package dbtarzan.gui.config.global

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.{ GridPane, ColumnConstraints, Priority }
import scalafx.scene.control.{ ComboBox, ListCell, Label }
import scalafx.geometry.{ Insets, HPos }
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import dbtarzan.config.global.GlobalData
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import scalafx.Includes._
import dbtarzan.localization.{ Languages, Language }

/**
  table + constraint input box + foreign keys
*/
class GlobalEditor(data : GlobalData
  ) extends TControlBuilder {
  
  val languages = ObservableBuffer(Languages.languages)

  val cmbLanguages = new ComboBox[Language] {
    items = languages
    editable = false
    value = data.language
    cellFactory = { _ => buildLanguageCell() }
    buttonCell = buildLanguageCell()
  }


 private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.ALWAYS
      })
    add(new Label { text = "Language:" }, 0, 0)
    add(cmbLanguages, 1, 0)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  private val buttons = new GlobalButtons() 

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
    if(JFXUtil.areYouSure("Are you sure you want to save the global settings?", "Save global settings"))
      try { save(GlobalData(
        cmbLanguages.getSelectionModel().selectedItem()
      )) } 
      catch {
        case ex : Exception => JFXUtil.showErrorAlert("Saving the global settings got: ", ex.getMessage())
      }
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure("Are you sure you want to close without saving?", "Cancel"))
        cancel()
  }

  def onSave(save : GlobalData  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def control : Parent = layout
}