package dbtarzan.gui.actor

import akka.actor.Actor
import dbtarzan.gui.TDatabases
import dbtarzan.messages._
import scalafx.application.Platform

class GUIWorker(databases : TDatabases) extends Actor {
  def receive = {
    case rsp : ResponseRows =>  Platform.runLater { databases.addRows(rsp) }

    case rsp: ResponseTables => Platform.runLater { databases.addTables(rsp) }

    case rsp : ResponseColumns => Platform.runLater { databases.addColumns(rsp) }

    case rsp: ResponseForeignKeys => Platform.runLater { databases.addForeignKeys(rsp) }	
	
    case rsp: ResponseColumnsFollow => Platform.runLater { databases.addColumnsFollow(rsp) }

	}

}