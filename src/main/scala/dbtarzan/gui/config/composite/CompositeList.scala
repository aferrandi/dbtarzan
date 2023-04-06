package dbtarzan.gui.config.composite

import dbtarzan.db.{Composite, CompositeId}
import dbtarzan.gui.TControlBuilder
import dbtarzan.localization.Localization
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.Parent
import scalafx.scene.control._

case class CompositeErrors(compositeId: CompositeId, errors : List[String])

/* The list of database to choose from */
class CompositeList(composites : List[Composite], localization : Localization) extends TControlBuilder {
  private val compositesObservable = ObservableBuffer(
    if(composites.nonEmpty) composites else List(newComposite())
  )
  private val menuAddComposite = new MenuItem(localization.addConnection)
  private val menuSave = new MenuItem(localization.save)
  private val list = new ListView[Composite](compositesObservable) {
  	SplitPane.setResizableWithParent(this, value = false)
  	contextMenu = new ContextMenu(menuAddComposite, menuSave)
    cellFactory = { _ => buildCell() }
  }

  def addNew():Unit={
    compositesObservable.append(newComposite())
    selectionModel().selectLast()
  }

  def selectFirst() : Unit = selectionModel().selectFirst()

  private def selectionModel() = list.selectionModel()

  private val newCompositeName = "<NEW>"

  def newComposite(): Composite = Composite(CompositeId(newCompositeName), List())
  /* returns Some(selected index) if it makes sense (> )0), None otherwise */
  def getSelectedIndex(): Option[Int] = {
    var index = Some(list.selectionModel().selectedIndex()).filter(_ >= 0)
    // println("Selected index:"+index)
    index
  }

  def onCompositeSelected(use : Composite => Unit) : Unit =
    selectionModel().selectedIndex.onChange {  (item, oldIndex, newIndex) => {
        //println("Selected index changed to "+newIndex) 
        Option(newIndex).map(_.intValue()).filter(_ >= 0).foreach(index => use(compositesObservable(index)))
      }}

  private def buildCell() = new ListCell[Composite] {
    item.onChange { (value , oldValue, newValue) => {
        val optValue = Option(newValue)
        // the orElse is to avoid problems when removing items
        val valueOrEmpty = optValue.map(_.compositeId.compositeName).orElse(Some(""))
        valueOrEmpty.foreach({ text.value = _ })
      }}}

  /* there must be always at least one connection */
  def removeCurrent() : Unit = 
    if(compositesObservable.nonEmpty)
      getSelectedIndex().foreach(selectedIndex => {
        compositesObservable.remove(selectedIndex)
        newSelectedIndex(selectedIndex).foreach(selectionModel().select(_))
      })

  /* returns errors validating the items in the list */
  def validate() : List[CompositeErrors] = {
    compositesObservable.toList.map(data => CompositeErrors(data.compositeId, CompositeValidation.validate(data)))
      .filter(_.errors.nonEmpty)
  }


  private def newSelectedIndex(selectedIndex : Int) : Option[Int] =
    if(compositesObservable.isEmpty)
      None
    else if(selectedIndex < compositesObservable.length)
      Some(selectedIndex)
    else
      Some(compositesObservable.length - 1)

  def content() : List[Composite] = compositesObservable.toList

  def changeSelected(data : Composite) : Unit =
    getSelectedIndex().foreach(selectedIndex => {
      // println("Before setconnectionDatas of "+selectedIndex+":"+data)
      compositesObservable.update(selectedIndex, data)
      selectionModel().select(selectedIndex) // patch to avoid deselection when changing data
      // println("After setconnectionDatas")
    })

  def control : Parent = list
}