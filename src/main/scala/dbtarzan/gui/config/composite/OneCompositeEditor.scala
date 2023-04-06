package dbtarzan.gui.config.composite

import dbtarzan.db.{Composite, CompositeId, DatabaseId}
import dbtarzan.gui.TControlBuilder
import dbtarzan.gui.util.{ListViewAddFromComboBuilder, OnChangeSafe, TComboStrategy}
import dbtarzan.localization.Localization
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Parent
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority}
import scalafx.scene.paint.Color

/* The editor for one single connection */
class OneCompositeEditor(
  allDatabaseId : List[DatabaseId],
  localization: Localization
  ) extends TControlBuilder {
  val safe = new OnChangeSafe()
  private val txtName = new TextField {
    text = ""
  }

  private val showText: Option[DatabaseId] => Label = (value: Option[DatabaseId]) => new Label {
    textFill = Color.Black
    text = value.map(databaseId => databaseId.databaseName).getOrElse("")
  }
  private val comboStrategy = new TComboStrategy[DatabaseId] {
    override def removeFromCombo(comboBuffer: ObservableBuffer[DatabaseId], item: DatabaseId): Unit = comboBuffer -= item

    override def addToCombo(comboBuffer: ObservableBuffer[DatabaseId], item: DatabaseId): Unit = comboBuffer += item
  }
  private val lvwDatabaseId = ListViewAddFromComboBuilder.buildUnordered[DatabaseId](localization.add, showText, comboStrategy)
  lvwDatabaseId.setComboData(allDatabaseId)

  private val grid =  new GridPane {
    columnConstraints = List(
      new ColumnConstraints() {},
      new ColumnConstraints() {
        hgrow = Priority.Always
      })
    add(new Label { text = localization.name+":" }, 0, 0)
    add(txtName, 1, 0)
    add(new Label { text = "Database ids:"; alignmentInParent = Pos.TopLeft }, 0, 1)
    add(lvwDatabaseId.control, 1, 1)
    padding = Insets(10)
    vgap = 10
    hgap = 10
  }

  def show(composite : Composite) : Unit =  safe.noChangeEventDuring(() => {
    txtName.text = composite.compositeId.compositeName
    lvwDatabaseId.setListData(composite.databaseIds)
  })


  def toComposite: Composite = Composite(
        CompositeId(txtName.text()),
        lvwDatabaseId.listData()
    )

  def control : Parent = grid

  def onChanged(useData : Composite => Unit) : Unit = {
      txtName.text.onChange(safe.onChange(() => useData(toComposite)))
      lvwDatabaseId.onChange(_ => safe.onChange(() => useData(toComposite)))
  }
}

