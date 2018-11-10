package dbtarzan.gui

import akka.actor.ActorRef
import scalafx.scene.control.{ContextMenu, Menu, MenuItem}
import scalafx.event.ActionEvent
import scalafx.Includes._
import dbtarzan.messages.{ CopySelectionToClipboard, QueryId }

class TableContextMenu(queryId : QueryId, guiActor : ActorRef) {
    def buildContextMenu() = new ContextMenu(
      new Menu("Copy selection to clipboard") {
        items = List(
          new MenuItem("Only cells") {
            onAction = (ev: ActionEvent) =>  guiActor ! CopySelectionToClipboard(queryId, false) 
          },
          new MenuItem("Cells with headers") {
            onAction = (ev: ActionEvent) =>  guiActor ! CopySelectionToClipboard(queryId, true)
          }
        )
      }) 
}
