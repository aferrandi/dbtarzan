package dbtarzan.gui

import akka.actor.ActorRef
import scalafx.scene.control.{ContextMenu, Menu, MenuItem}
import scalafx.event.ActionEvent
import scalafx.Includes._
import dbtarzan.messages.{ CopySelectionToClipboard, TableId }

class TableContextMenu(tableId : TableId, guiActor : ActorRef) {
    def buildContextMenu() = new ContextMenu(
      new Menu("Copy selection to clipboard") {
        items = List(
          new MenuItem("Only cells") {
            onAction = (ev: ActionEvent) =>  guiActor ! CopySelectionToClipboard(tableId, false) 
          },
          new MenuItem("Cells with headers") {
            onAction = (ev: ActionEvent) =>  guiActor ! CopySelectionToClipboard(tableId, true)
          }
        )
      }) 
}
