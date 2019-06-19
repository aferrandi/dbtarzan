package dbtarzan.gui.foreignkeys

import scalafx.scene.layout.BorderPane
import scalafx.scene.Parent
import akka.actor.ActorRef

import dbtarzan.db.{TableNames, ForeignKeysForTableList, ForeignKey, Fields, DatabaseId}
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
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
  private val layout = new BorderPane {
    top = keysTable.control
    center = singleEditor.control
    bottom = buttons.control
  }
  buttons.onNew(() => singleEditor.showNew())
  buttons.onRemove(() => keysTable.removeSelected())

  private def saveIfPossible(save : List[ForeignKey]  => Unit) : Unit = {
      if(JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
        try { save(keysTable.currentForeignKeys()) } 
        catch {
          case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", ex.getMessage())
        }
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        cancel()
  }

  def onSave(save : List[ForeignKey]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  

  def handleForeignKeys(additionalKeys : ForeignKeysForTableList) : Unit = 
    keysTable.addRows(additionalKeys.keys.flatMap(_.keys.keys))

  def handleColumns(tableName : String, columns : Fields) : Unit =
    singleEditor.handleColumns(tableName, columns) 

  def control : Parent = layout
}