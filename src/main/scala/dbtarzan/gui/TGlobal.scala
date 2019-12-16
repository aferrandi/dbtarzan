package dbtarzan.gui

import dbtarzan.messages.ResponseTestConnection

trait TGlobal {
  def handleTestConnectionResponse(rsp: ResponseTestConnection) : Unit
}
