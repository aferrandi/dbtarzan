package dbtarzan.gui.config.composite

import dbtarzan.db.{Composite, CompositeId}
import dbtarzan.gui.interfaces.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control._

case class CompositeErrors(compositeId: CompositeId, errors : List[String])

object CompositeList {
 val newCompositeName = "<NEW>"
}


/* The list of database to choose from */
class CompositeList(composites : List[Composite], localization : Localization) extends TControlBuilder {
  private val compositesWithNew: List[Composite] = if (composites.nonEmpty) composites else List(newComposite())
  private val compositesObservable : ObservableBuffer[Composite]= ObservableBuffer.from[Composite](compositesWithNew)
  private val menuAddComposite = new MenuItem(localization.addConnection)
  private val menuSave = new MenuItem(localization.save)
  private val list = new ListView[Composite](compositesObservable) {
  	SplitPane.setResizableWithParent(this, value = false)
  	contextMenu = new ContextMenu(menuAddComposite, menuSave)
    cellFactory = (cell, value) => cell.text.value  = value.compositeId.compositeName
  }

  def addNew():Unit={
    compositesObservable.append(newComposite())
    selectionModel().selectLast()
  }

  def selectFirst() : Unit = selectionModel().selectFirst()

  private def selectionModel() = list.selectionModel()

  private def newComposite(): Composite = Composite(CompositeId(CompositeList.newCompositeName), List.empty)

  /* returns Some(selected index) if it makes sense (> )0), None otherwise */
  private def selectedIndex(): Option[Int] = Some(list.selectionModel().selectedIndex()).filter(_ >= 0)
  def onCompositeSelected(use : Composite => Unit) : Unit =
    selectionModel().selectedIndex.onChange {  (_, _, newIndex) => {
        //println("Selected index changed to "+newIndex)
        Option(newIndex).map(_.intValue()).filter(_ >= 0).foreach(index => use(compositesObservable(index)))
      }}

  /* there must be always at least one connection */
  def removeCurrent() : Unit =
    if(compositesObservable.nonEmpty)
      selectedIndex().foreach(selectedIndex => {
        compositesObservable.remove(selectedIndex)
        newSelectedIndex(selectedIndex).foreach(selectionModel().select(_))
      })

  /* returns errors validating the items in the list */
  def validate() : List[CompositeErrors] =
    compositesObservable.toList
      .map(data => CompositeErrors(data.compositeId, CompositeValidation.validate(data)))
      .filter(_.errors.nonEmpty)

  private def newSelectedIndex(selectedIndex : Int) : Option[Int] =
    if(compositesObservable.isEmpty)
      None
    else if(selectedIndex < compositesObservable.length)
      Some(selectedIndex)
    else
      Some(compositesObservable.length - 1)

  def content() : List[Composite] = compositesObservable.toList

  def changeSelected(data : Composite) : Unit =
    selectedIndex().foreach(selectedIndex => {
      // println("Before setconnectionDatas of "+selectedIndex+":"+data)
      compositesObservable.update(selectedIndex, data)
      selectionModel().select(selectedIndex) // patch to avoid deselection when changing data
      // println("After setconnectionDatas")
    })

  def control : Parent = list
}