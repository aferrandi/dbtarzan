package dbtarzan.gui.foreignkeys

import org.apache.pekko.actor.ActorRef
import dbtarzan.db.{DatabaseId, Fields, TableId, VirtualalForeignKey}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.util.{JFXUtil, StringUtil}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.UpdateVirtualForeignKeys
import scalafx.geometry.Orientation
import scalafx.scene.Parent
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane


/* table + constraint input box + foreign keys */
class VirtualForeignKeysEditor(
                                   dbActor : ActorRef,
                                   databaseId: DatabaseId,
                                   tableIds: List[TableId],
                                   localization: Localization,
                                   log: Logger
  ) extends TControlBuilder {
  private val keysTable = new ForeignKeysTable(databaseId, localization, log)
  private val singleEditor = new SingleEditor(dbActor, tableIds, localization)
  private val buttons = new VirtualForeignKeysButtons(localization)
  private val bottomPane = new BorderPane {
    center = singleEditor.control
    bottom = buttons.control
  }
  private val layout = new SplitPane {
    items ++= List(keysTable.control, bottomPane)
    orientation() =  Orientation.Vertical
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
      val res = VirtualKeysVerification.verify(keys)
      if(res.correct) {
        if(JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
          try { 
            dbActor ! UpdateVirtualForeignKeys(databaseId, keys)
            close()
          } 
          catch {
            case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", ex.getMessage())
          }
        } else 
          JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", 
            localization.errorVFKerification + " " +
            StringUtil.textIf(res.nameEmpty, () => localization.errorAFKEmptyNames +". ") +
            StringUtil.textIf(res.nameNewRow, () => localization.errorAFKNameNewRow +". ") +
            StringUtil.textIf(res.noColumns.nonEmpty, () => localization.errorAFKNoColumns(res.noColumns) +". ") +
            StringUtil.textIf(res.sameColumns.nonEmpty, () => localization.errorAFKSameColumns(res.sameColumns) +". ") +
            StringUtil.textIf(res.differentColumnsNumber.nonEmpty, () => localization.errorAFKDifferentColumnsNumber(res.differentColumnsNumber) +". ") +
            StringUtil.textIf(res.nameDuplicates.nonEmpty, () => localization.errorAFKDuplicateNames(res.nameDuplicates) +". ") +
            StringUtil.textIf(res.relationDuplicates.nonEmpty, () => localization.errorAFKDuplicateRelations(res.relationDuplicates) +". ")
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

  def handleForeignKeys(virtualKeys : List[VirtualalForeignKey]) : Unit =
    keysTable.addRows(virtualKeys)

  def handleColumns(tableId : TableId, columns : Fields) : Unit =
    singleEditor.handleColumns(tableId, columns)

  def control : Parent = layout
}