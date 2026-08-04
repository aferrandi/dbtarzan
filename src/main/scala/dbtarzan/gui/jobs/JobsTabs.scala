package dbtarzan.gui.jobs;

import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.db.*
import dbtarzan.db.foreignkeys.ForeignKeyMapper
import dbtarzan.gui.BrowsingTable
import dbtarzan.gui.database.Job
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.gui.tabletabs.{TTableWithTab, TableStructureText, TableTabsMap, TabsToClose}
import dbtarzan.localization.Localization
import dbtarzan.log.actor.Logger
import dbtarzan.messages.{ QueryId, TWithTableId, TWithQueryId, TWithJobId, ResponseCloseTables }
import org.apache.pekko.actor.ActorRef
import scalafx.scene.control.{Tab, TabPane, Tooltip, Label}
import scalafx.scene.Parent
import scalafx.geometry.Side
import scalafx.event.Event
import scalafx.Includes.*

case class OriginalTabSize(maxHeight: Double, minHeight: Double, maxWidth: Double, minWidth: Double)

class JobsTabs() extends TControlBuilder {
    private val tabs = new TabPane {
        side = Side.Left
        rotateGraphic = false
        visible = false

    }
    val originalSize = OriginalTabSize(tabs.tabMaxHeight(), tabs.tabMinHeight(), tabs.tabMaxWidth(), tabs.tabMinWidth())
    val zeroSize = OriginalTabSize(0, 0, 0, 0)
    setTabSize(zeroSize)
    tabs += spacingTab()

    private def spacingTab() = new Tab {
        closable = false
        style = "-fx-pref-width: 30px; -fx-background-color: transparent;"
    }

    def addTab(tab: Tab): Unit = {
        tabs.visible = true
        tabs += tab
        tabs.selectionModel().select(tab)
        resetTabPaneSize()
    }

    private def resetTabPaneSize(): Unit = {
        val tabSize = newTabPaneSize()
        setTabSize(tabSize)
    }

    private def setTabSize(tabSize: OriginalTabSize): Unit = {
        tabs.tabMaxHeight = tabSize.maxHeight
        tabs.tabMinHeight = tabSize.minHeight
        tabs.tabMaxWidth = tabSize.maxWidth
        tabs.tabMinWidth = tabSize.minWidth
    }

    private def newTabPaneSize(): OriginalTabSize =
        if (tabs.tabs.size > 2)
            originalSize
        else
            zeroSize

    def removeTab(tab: Tab) : Unit = {
        tabs.tabs -= tab
        resetTabPaneSize()
        refreshAfterRemoveTab()
    }

    def currentTab(): Tab = tabs.selectionModel().selectedItem()

    def control : Parent = tabs

    /* needed because otherwise the tab content is just gray */
    def refreshAfterRemoveTab(): Unit =
        if(!tabs.tabs.isEmpty)
            tabs.selectionModel().selectLast()
}

