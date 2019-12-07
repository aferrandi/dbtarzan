package dbtarzan.gui.browsingtable

import scalafx.scene.control.{MenuItem, Menu, MenuBar, CheckMenuItem }
import scalafx.event.ActionEvent
import scalafx.scene.input.{ KeyEvent, KeyCodeCombination, KeyCombination, KeyCode }
import scalafx.Includes._
import akka.actor.ActorRef

import dbtarzan.gui.util.JFXUtil
import dbtarzan.messages._
import dbtarzan.localization.Localization

/* the code to build the tabs menu and the related key combinations */ 
object TableMenu {
    val CLOSE_TAB_BEFORE_KEY = new KeyCodeCombination(KeyCode.B, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CLOSE_TAB_AFTER_KEY = new KeyCodeCombination(KeyCode.F, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_ALL_KEY = new KeyCodeCombination(KeyCode.A, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_NONE_KEY = new KeyCodeCombination(KeyCode.N, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val ROW_DETAILS_KEY = new KeyCodeCombination(KeyCode.R, KeyCombination.ControlDown)

    private def menuItem(text: String, code: KeyCodeCombination, ev : ActionEvent => Unit) : MenuItem =
        new MenuItem(text+" ("+code.displayText+")") {
            onAction = ev
        }
    private def menuItem(text: String, ev : ActionEvent => Unit) : MenuItem =
        new MenuItem(text) {
            onAction = ev
        }
    private def checkMenuItem(text: String, code: KeyCodeCombination, ev : ActionEvent => Unit) : MenuItem =
        new CheckMenuItem(text+" ("+code.displayText+")") {
            onAction = ev
        }

    def buildMainMenu(guiActor: ActorRef, queryId : QueryId, localization : Localization) = new MenuBar {
        menus = List(
            new Menu(JFXUtil.threeLines) {
                items = List(
                    menuItem(localization.closeTabsBeforeThis, CLOSE_TAB_BEFORE_KEY, (ev: ActionEvent) => guiActor ! RequestRemovalTabsBefore(queryId)),
                    menuItem(localization.closeTabsAfterThis, CLOSE_TAB_AFTER_KEY, (ev: ActionEvent) => guiActor ! RequestRemovalTabsAfter(queryId)),
                    menuItem(localization.closeAllTabs, (ev: ActionEvent) => guiActor ! RequestRemovalAllTabs(queryId.tableId.databaseId)),
                    menuItem(localization.checkAll, CHECK_ALL_KEY, (ev: ActionEvent) => guiActor ! CheckAllTableRows(queryId)),
                    menuItem(localization.uncheckAll, CHECK_NONE_KEY, (ev: ActionEvent) => guiActor ! CheckNoTableRows(queryId)),
                    checkMenuItem(localization.rowDetails, ROW_DETAILS_KEY, (ev: ActionEvent) => guiActor ! SwitchRowDetails(queryId)),
                )
            }
        )
        stylesheets += "orderByMenuBar.css"
    }  
    /* to get the tableid is an expensive operation, therefore we use it as a closure */
    def handleKeyCombination(guiActor: ActorRef, ev: KeyEvent, tableId : () => Option[QueryId]) : Unit =
        if(ev.controlDown) {
            if(CLOSE_TAB_BEFORE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsBefore(id))
            else if(CLOSE_TAB_AFTER_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsAfter(id))
            else if(CHECK_ALL_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckAllTableRows(id))
            else if(CHECK_NONE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckNoTableRows(id))
            else if(ROW_DETAILS_KEY.`match`(ev)) tableId().foreach(id => guiActor ! SwitchRowDetails(id))
        }
}