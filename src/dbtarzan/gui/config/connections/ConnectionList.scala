package dbtarzan.gui.config.connections

import scalafx.scene.control.{ ListView, ListCell, SplitPane, ContextMenu, MenuItem }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.Includes._

import dbtarzan.gui.TControlBuilder
import dbtarzan.config.connections.ConnectionData
import dbtarzan.config.Password
import dbtarzan.localization.Localization

case class ConnectionDataErrors(name : String, errors : List[String])

/* The list of database to choose from */
class ConnectionList(connectionDatasRead : List[ConnectionData], localization : Localization) extends TControlBuilder {
  private val connectionDatas = ObservableBuffer(
    if(!connectionDatasRead.isEmpty) connectionDatasRead else List(newData())
  )
  private val menuAddConnection = new MenuItem(localization.addConnection)
  private val menuSave = new MenuItem(localization.save)
  private val list = new ListView[ConnectionData](connectionDatas) {
  	SplitPane.setResizableWithParent(this, false) 
  	contextMenu = new ContextMenu(menuAddConnection, menuSave)   
    cellFactory = { _ => buildCell() }
  }

  def addNew():Unit={
    println("addNew")
    connectionDatas.append(newData())
    selectionModel().selectLast()   
  }

  def selectFirst() : Unit = selectionModel().selectFirst()

  private def selectionModel() = list.selectionModel() 

  def newData() = ConnectionData("", "<NEW>", "","",None,"","",None, None, None, None, None)
  /* returns Some(selected index) if it makes sense (> )0), None otherwise */
  def getSelectedIndex() = {
    var index = Some(list.selectionModel().selectedIndex()).filter(_ >= 0)
    // println("Selected index:"+index)
    index
  }

  def onConnectionSelected(use : ConnectionData => Unit) : Unit = 
    selectionModel().selectedIndex.onChange {  (item, oldIndex, newIndex) => {
        //println("Selected index changed to "+newIndex) 
        Option(newIndex).map(_.intValue()).filter(_ >= 0).foreach(index => use(connectionDatas(index)))
      }}

  private def buildCell() = new ListCell[ConnectionData] {
    item.onChange { (value , oldValue, newValue) => {
        val optValue = Option(newValue)
        // the orElse is to avoid problems when removing items
        val valueOrEmpty = optValue.map(_.name).orElse(Some(""))
        valueOrEmpty.foreach({ text.value = _ })
      }}}

  /* there must be always at least one connection */
  def removeCurrent() : Unit = 
    if(connectionDatas.length > 1) 
      getSelectedIndex().foreach(selectedIndex => {
        println("Remove current")
        connectionDatas.remove(selectedIndex)
        newSelectedIndex(selectedIndex).foreach(selectionModel().select(_))
      })

  def duplicateCurrent() : Unit = 
    if(connectionDatas.length > 1) 
      getSelectedIndex().foreach(selectedIndex => {
        println("Duplicate current")
        val toDuplicate = connectionDatas(selectedIndex)
        connectionDatas += toDuplicate.copy(name = "<NEW>")
        selectionModel().selectLast()   
      })

  
  /* returns errors validating the items in the list */
  def validate() : List[ConnectionDataErrors] = {
    println("Validate")
    connectionDatas.toList.map(data => ConnectionDataErrors(data.name, ConnectionDataValidation.validate(data)))
      .filter(!_.errors.isEmpty)
  }


  private def newSelectedIndex(selectedIndex : Int) : Option[Int] =
    if(connectionDatas.isEmpty)
      None
    else if(selectedIndex < connectionDatas.length)
      Some(selectedIndex)
    else
      Some(connectionDatas.length - 1)

  def content() : List[ConnectionData] = connectionDatas.toList

  def changeSelected(data : ConnectionData) : Unit = 
    getSelectedIndex().foreach(selectedIndex => {
      // println("Before setconnectionDatas of "+selectedIndex+":"+data)
      connectionDatas.update(selectedIndex, data)
      selectionModel.select(selectedIndex) // patch to avoid deselection when changing data    
      // println("After setconnectionDatas")
    })

  def control : Parent = list
}