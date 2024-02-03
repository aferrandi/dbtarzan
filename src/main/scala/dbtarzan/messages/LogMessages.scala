package dbtarzan.messages

import java.time.LocalDateTime

sealed trait TLogMessage{ def produced : LocalDateTime; def text: String }

sealed trait TLogMessageGUI { def produced: LocalDateTime; def text: String}


case class Error(produced : LocalDateTime, text: String, ex : Option[Exception]) extends TLogMessage, TLogMessageGUI

case class Warning(produced : LocalDateTime, text : String) extends TLogMessage, TLogMessageGUI

case class Info(produced : LocalDateTime, text : String) extends TLogMessage, TLogMessageGUI

case class Debug(produced : LocalDateTime, text : String) extends TLogMessage

