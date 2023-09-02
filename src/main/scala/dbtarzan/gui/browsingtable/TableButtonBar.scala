package dbtarzan.gui.browsingtable

import dbtarzan.localization.Localization
import dbtarzan.messages.*
import org.apache.pekko.actor.ActorRef
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.Node
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Button, ButtonBar, ToggleButton}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination, KeyEvent}
import scalafx.scene.layout.HBox

/* the code to build the tabs menu and the related key combinations */ 
object TableButtonBar {
    val CLOSE_THIS_TAB = new KeyCodeCombination(KeyCode.Delete, KeyCombination.ControlDown)
    val CLOSE_TAB_BEFORE_KEY = new KeyCodeCombination(KeyCode.B, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CLOSE_TAB_AFTER_KEY = new KeyCodeCombination(KeyCode.F, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_ALL_KEY = new KeyCodeCombination(KeyCode.A, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val CHECK_NONE_KEY = new KeyCodeCombination(KeyCode.N, KeyCombination.ControlDown, KeyCombination.ShiftDown)
    val ROW_DETAILS_KEY = new KeyCodeCombination(KeyCode.R, KeyCombination.ControlDown)

    private def button(text: String, code: KeyCodeCombination, ev : ActionEvent => Unit) : Button =
      new Button(text) {
        onAction = ev
        tooltip = code.displayText
      }

    private def button(text: String, ev : ActionEvent => Unit) : Button =
      new Button(text) {
            onAction = ev
        }

    private def toggleButton(text: String, code: KeyCodeCombination, ev : ActionEvent => Unit) : ToggleButton =
      new ToggleButton(text) {
          onAction = ev
          tooltip = code.displayText
        }

    def buildButtonBar(guiActor: ActorRef, queryId : QueryId, localization : Localization): HBox = new HBox() {
      children = List(
        button(localization.closeThisTab, CLOSE_THIS_TAB, (_: ActionEvent) => guiActor ! RequestRemovalThisTab(queryId)),
        button(localization.closeTabsBeforeThis, CLOSE_TAB_BEFORE_KEY, (_: ActionEvent) => guiActor ! RequestRemovalTabsBefore(queryId)),
        button(localization.closeTabsAfterThis, CLOSE_TAB_AFTER_KEY, (_: ActionEvent) => guiActor ! RequestRemovalTabsAfter(queryId)),
        button(localization.closeAllTabs, (_: ActionEvent) => guiActor ! RequestRemovalAllTabs(queryId.tableId.databaseId)),
        button(localization.checkAll, CHECK_ALL_KEY, (_: ActionEvent) => guiActor ! CheckAllTableRows(queryId)),
        button(localization.uncheckAll, CHECK_NONE_KEY, (_: ActionEvent) => guiActor ! CheckNoTableRows(queryId)),
        toggleButton(localization.rowDetails, ROW_DETAILS_KEY, (_: ActionEvent) => guiActor ! SwitchRowDetails(queryId))
      )
      spacing = 5
    }
    /* to get the tableid is an expensive operation, therefore we use it as a closure */
    def handleKeyCombination(guiActor: ActorRef, ev: KeyEvent, tableId : () => Option[QueryId]) : Unit =
        if(ev.controlDown) {
            if(CLOSE_THIS_TAB.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalThisTab(id))
            else if(CLOSE_TAB_BEFORE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsBefore(id))
            else if(CLOSE_TAB_AFTER_KEY.`match`(ev)) tableId().foreach(id => guiActor ! RequestRemovalTabsAfter(id))
            else if(CHECK_ALL_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckAllTableRows(id))
            else if(CHECK_NONE_KEY.`match`(ev)) tableId().foreach(id => guiActor ! CheckNoTableRows(id))
            else if(ROW_DETAILS_KEY.`match`(ev)) tableId().foreach(id => guiActor ! SwitchRowDetails(id))
        }
}