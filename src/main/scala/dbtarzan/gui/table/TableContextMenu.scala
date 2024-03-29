package dbtarzan.gui.table

import org.apache.pekko.actor.ActorRef
import scalafx.scene.control.{ContextMenu, Menu, MenuItem}
import scalafx.event.ActionEvent
import scalafx.Includes._
import dbtarzan.messages.{ CopySelectionToClipboard, QueryId }
import dbtarzan.localization.Localization

class TableContextMenu(queryId : QueryId, guiActor : ActorRef, localization : Localization) {
    def buildContextMenu() = new ContextMenu(
      new Menu(localization.copySelectionToClipboard) {
        items = List(
          new MenuItem(localization.onlyCells) {
            onAction = (_: ActionEvent) =>  guiActor ! CopySelectionToClipboard(queryId, false)
          },
          new MenuItem(localization.cellsWithHeaders) {
            onAction = (_: ActionEvent) =>  guiActor ! CopySelectionToClipboard(queryId, true)
          }
        )
      }) 
}