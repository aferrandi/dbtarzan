package dbtarzan.gui.config.connections

import scalafx.scene.control.{ ListView, SplitPane, ContextMenu, MenuItem }
import scalafx.scene.Parent
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import dbtarzan.config.connections.ConnectionData
import dbtarzan.localization.Localization
import dbtarzan.config.password.Password
import dbtarzan.gui.interfaces.TControlBuilder

case class ConnectionDataErrors(name : String, errors : List[String])

/* The list of database to choose from */
class ConnectionList(connectionDatasRead : List[ConnectionData], localization : Localization) extends TControlBuilder {
  private val connectionDatas : ObservableBuffer[ConnectionData] = ObservableBuffer.from[ConnectionData](
    if(connectionDatasRead.nonEmpty) connectionDatasRead else List(newData())
  )
  private val menuAddConnection = new MenuItem(localization.addConnection)
  private val menuSave = new MenuItem(localization.save)
  private val list = new ListView[ConnectionData](connectionDatas) {
    SplitPane.setResizableWithParent(this, value = false)
    contextMenu = new ContextMenu(menuAddConnection, menuSave)   
    cellFactory = (cell, value) => {
        cell.text.value = value.name
    }
  }

  def addNew():Unit={
    connectionDatas.append(newData())
    selectionModel().selectLast()   
  }

  def selectFirst() : Unit = selectionModel().selectFirst()

  private def selectionModel() = list.selectionModel()

  private val newConnectionName = "<NEW>"

  def newData(): ConnectionData = ConnectionData("", newConnectionName, "","",None,"", Some(Password("")),None, None, None, None, None, Some(1000), None)
  /* returns Some(selected index) if it makes sense (> )0), None otherwise */
  def retrieveSelectedIndex(): Option[Int] = {
    val index = Some(list.selectionModel().selectedIndex()).filter(_ >= 0)
    // println("Selected index:"+index)
    index
  }

  def onConnectionSelected(use : ConnectionData => Unit) : Unit =
    selectionModel().selectedIndex.onChange {  (_, _, newIndex) => {
        //println("Selected index changed to "+newIndex)
        Option(newIndex).map(_.intValue()).filter(_ >= 0).foreach(index => use(connectionDatas(index)))
      }}

  /* there must be always at least one connection */
  def removeCurrent() : Unit =
    if(connectionDatas.nonEmpty)
      retrieveSelectedIndex().foreach(selectedIndex => {
        connectionDatas.remove(selectedIndex)
        newSelectedIndex(selectedIndex).foreach(selectionModel().select(_))
      })

  def duplicateCurrent() : Unit =
    if(connectionDatas.nonEmpty)
      retrieveSelectedIndex().foreach(selectedIndex => {
        val toDuplicate = connectionDatas(selectedIndex)
        connectionDatas += toDuplicate.copy(name = newConnectionName)
        selectionModel().selectLast()
      })

  /* returns errors validating the items in the list */
  def validate() : List[ConnectionDataErrors] = {
    connectionDatas.toList.map(data => ConnectionDataErrors(data.name, ConnectionDataValidation.validate(data)))
      .filter(_.errors.nonEmpty)
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
    retrieveSelectedIndex().foreach(selectedIndex => {
      // println("Before setconnectionDatas of "+selectedIndex+":"+data)
      connectionDatas.update(selectedIndex, data)
      selectionModel().select(selectedIndex) // patch to avoid deselection when changing data
      // println("After setconnectionDatas")
    })

  def control : Parent = list
}