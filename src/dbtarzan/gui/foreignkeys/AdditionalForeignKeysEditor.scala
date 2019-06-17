package dbtarzan.gui.foreignkeys

import scalafx.scene.layout.BorderPane
import scalafx.scene.Parent
import akka.actor.ActorRef

import dbtarzan.db.{TableNames, ForeignKeysForTableList, ForeignKey}
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization

// additionalKeys.keys.flatMap(_.keys.keys),

/* table + constraint input box + foreign keys */
class AdditionalForeignKeysEditor(
  guiActor: ActorRef,
  localization: Localization
  ) extends TControlBuilder {
  private val keysTable = new ForeignKeysTable(guiActor, localization)
  private val oneTable = new SingleEditor(localization)
  private val buttons = new AdditionalButtons(localization)
  private val layout = new BorderPane {
    top = keysTable.control
    center = oneTable.control
    bottom = buttons.control
  }


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

  def control : Parent = layout
}