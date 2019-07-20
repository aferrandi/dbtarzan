package dbtarzan.gui.foreignkeys

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.geometry.Orientation
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{TableNames, AdditionalForeignKey, Fields, DatabaseId}
import dbtarzan.gui.util.StringUtil
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages.UpdateAdditionalForeignKeys
import dbtarzan.localization.Localization

/* table + constraint input box + foreign keys */
class AdditionalForeignKeysEditor(
  dbActor : ActorRef,
  guiActor: ActorRef,
  databaseId: DatabaseId,
  tableNames: TableNames,
  localization: Localization
  ) extends TControlBuilder {
  private val keysTable = new ForeignKeysTable(guiActor, localization)
  private val singleEditor = new SingleEditor(dbActor, databaseId, tableNames, localization)
  private val buttons = new AdditionalButtons(localization)
  private val bottomPane = new BorderPane {
    center = singleEditor.control
    bottom = buttons.control
  }
  private val layout = new SplitPane {
    items ++= List(keysTable.control, bottomPane)
    orientation() =  Orientation.VERTICAL
    maxHeight = Double.MaxValue    
    maxWidth = Double.MaxValue
    dividerPositions = 0.5
    // SplitPane.setResizableWithParent(info.control, false)
  }
  buttons.onNew(() => keysTable.addEmptyRow())
  singleEditor.onChanged(key => keysTable.refreshSelected(key))
  keysTable.onSelected(key => singleEditor.show(key))

  private def saveIfPossible(close : () => Unit) : Unit = {
      val keys = keysTable.currentForeignKeys()
      val res = AdditionalKeysVerification.verify(keys)
      if(res.correct) {
        if(JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
          try { 
            dbActor ! UpdateAdditionalForeignKeys(databaseId, keys)
            close()
          } 
          catch {
            case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", ex.getMessage())
          }
        } else 
          JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", 
            localization.errorAFKVerification + 
            StringUtil.textIf(res.nameEmpty, () => localization.errorAFKEmptyNames +". ") +
            StringUtil.textIf(res.nameNewRow, () => localization.errorAFKNameNewRow +". ") +
            StringUtil.textIf(!res.noColumns.isEmpty, () => localization.errorAFKNoColumns(res.noColumns) +". ") +
            StringUtil.textIf(!res.sameColumns.isEmpty, () => localization.errorAFKSameColumns(res.sameColumns) +". ") +
            StringUtil.textIf(!res.nameDuplicates.isEmpty, () => localization.errorAFKDuplicateNames(res.nameDuplicates) +". ") +
            StringUtil.textIf(!res.relationDuplicates.isEmpty, () => localization.errorAFKDuplicateRelations(res.relationDuplicates) +". ")
          )
  }

  def cancelIfPossible(close : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        close()
  }

  def onClose(close : ()  => Unit): Unit = {
    buttons.onCancel(() => cancelIfPossible(close))
    buttons.onSave(() => saveIfPossible(close))
  }

  def handleForeignKeys(additionalKeys : List[AdditionalForeignKey]) : Unit = 
    keysTable.addRows(additionalKeys)

  def handleColumns(tableName : String, columns : Fields) : Unit =
    singleEditor.handleColumns(tableName, columns) 

  def control : Parent = layout
}