package dbtarzan.gui.foreignkeys

import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane
import scalafx.geometry.Orientation
import scalafx.scene.Parent
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.db.{TableNames, ForeignKeysForTableList, ForeignKey, Fields, DatabaseId, ForeignKeys, ForeignKeyDirection, ForeignKeysForTable}
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
  buttons.onRemove(() => keysTable.removeSelected())
  singleEditor.onChanged(key => keysTable.refreshSelected(key))
  keysTable.onSelected(key => singleEditor.show(key))

  private def saveIfPossible(close : () => Unit) : Unit = {
      if(JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
        try { dbActor ! UpdateAdditionalForeignKeys(
              databaseId,
              ForeignKeysForTableList(buildKeys(
                keysTable.currentForeignKeys()
              ))
          )
          close()
        } 
        catch {
          case ex : Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections+": ", ex.getMessage())
        }
  }

  private def buildKeys(keys: List[ForeignKey]) : List[ForeignKeysForTable] = {
    val keysTurned = keys.map(k => ForeignKey(k.name+"_turned", k.to, k.from, ForeignKeyDirection.TURNED))
    (keys ++ keysTurned).groupBy(_.from.table).map({ 
      case (table, keysForTable) =>  ForeignKeysForTable(table, ForeignKeys(keysForTable))
      }).toList
  }

  def cancelIfPossible(close : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        close()
  }

  def onClose(close : ()  => Unit): Unit = {
    buttons.onCancel(() => cancelIfPossible(close))
    buttons.onSave(() => saveIfPossible(close))
  }

  def handleForeignKeys(additionalKeys : ForeignKeysForTableList) : Unit = 
    keysTable.addRows(additionalKeys.keys.flatMap(_.keys.keys))

  def handleColumns(tableName : String, columns : Fields) : Unit =
    singleEditor.handleColumns(tableName, columns) 

  def control : Parent = layout
}