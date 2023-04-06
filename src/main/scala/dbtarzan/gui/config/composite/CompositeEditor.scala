package dbtarzan.gui.config.composite

import dbtarzan.db.{Composite, DatabaseId}
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.JFXUtil
import dbtarzan.localization.Localization
import scalafx.scene.Parent
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.BorderPane

/* table + constraint input box + foreign keys */
class CompositeEditor(
                       composites : List[Composite],
                       allDatabaseId : List[DatabaseId],
                       localization: Localization
  ) extends TControlBuilder {
  private val compositeList = new CompositeList(composites, localization)
  compositeList.onCompositeSelected(showComposite)
  private val oneCompositeEditor = new OneCompositeEditor(allDatabaseId, localization)
  oneCompositeEditor.onChanged(compositeList.changeSelected)
  private val buttons = new CompositeButtons(localization)

  private val layout = new BorderPane {
    center = buildSplitPane()
    bottom = buttons.control
  }
  buttons.onNew(() => compositeList.addNew())
  buttons.onRemove(() => compositeList.removeCurrent())
  // buffer.selectFirst()

  /* builds the split panel containing the table and the foreign keys list */
  private def buildSplitPane() = new SplitPane {
    maxHeight = Double.MaxValue
    maxWidth = Double.MaxValue
    items.addAll(compositeList.control, oneCompositeEditor.control)
    dividerPositions = 0.3
    SplitPane.setResizableWithParent(compositeList.control, value = false)
  }

  private def showComposite(composite: Composite): Unit = try {
    oneCompositeEditor.show(composite)
  } catch {
    case ex: Exception => JFXUtil.showErrorAlert(localization.errorDisplayingConnections + ": ", ex.getMessage)
  }

  private def saveIfPossible(save: List[Composite] => Unit): Unit = {
    val errors = compositeList.validate()
    if (errors.isEmpty) {
      if (JFXUtil.areYouSure(localization.areYouSureSaveConnections, localization.saveConnections))
        try {
          save(compositeList.content())
        }
        catch {
          case ex: Exception => JFXUtil.showErrorAlert(localization.errorSavingConnections + ": ", ex.getMessage)
        }
    } else
      showCompositeErrors(errors)
  }

  private def showCompositeErrors(errors: List[CompositeErrors]): Unit = {
    val errorText = errors.map(error => error.compositeId.compositeName + ":" + error.errors.mkString(",")).mkString(";")
    JFXUtil.showErrorAlert(localization.errorSavingConnections + ": ", errorText)
  }

  def cancelIfPossible(cancel : () => Unit) : Unit = {
    if(JFXUtil.areYouSure(localization.areYouSureClose, localization.cancel))
        cancel()
  }

  def onSave(save : List[Composite]  => Unit): Unit =
    buttons.onSave(() => saveIfPossible(save))

  def onCancel(cancel : ()  => Unit): Unit =
    buttons.onCancel(() => cancelIfPossible(cancel))

  def control : Parent = layout
}