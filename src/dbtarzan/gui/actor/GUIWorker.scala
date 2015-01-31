package dbtarzan.gui.actor

import akka.actor.Actor
import dbtarzan.gui.{ TDatabases, TErrors }
import dbtarzan.messages._
import scalafx.application.Platform

/*
    Receives messages from the other actors (DatabaseWorker and ConfigWorker) and thread-safely updates the GUI 
*/
class GUIWorker(databases : TDatabases, errors : TErrors) extends Actor {
  def receive = {
    case rsp : ResponseRows =>  Platform.runLater { databases.addRows(rsp) }

    case rsp: ResponseTables => Platform.runLater { databases.addTables(rsp) }

    case rsp : ResponseColumns => Platform.runLater { databases.addColumns(rsp) }

    case rsp: ResponseForeignKeys => Platform.runLater { databases.addForeignKeys(rsp) }	
	
    case rsp: ResponseColumnsFollow => Platform.runLater { databases.addColumnsFollow(rsp) }

    case rsp: ResponseDatabase => Platform.runLater { databases.addDatabase(rsp) } 

    case err : Error => Platform.runLater { errors.addError(err) }

    case err : ErrorDatabaseAlreadyOpen => Platform.runLater { 
                databases.showDatabase(err.databaseName)
                errors.addError(Error(new Exception("Database "+err.databaseName+" already open")))
            }
	}

}