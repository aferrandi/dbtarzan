package dbtarzan.messages 

import akka.actor.ActorRef
import java.time.LocalDateTime

/* acts as a classic "Logger" class, but sends the messages to the guiActor */
class Logger(guiActor : ActorRef) extends TLogger {
    override def debug(text : String) : Unit =
      println(text)

    override def info(text : String) : Unit =
        guiActor ! Info(LocalDateTime.now, text)

    override def warning(text : String) : Unit =
        guiActor ! Warning(LocalDateTime.now, text)

    override def error(text : String, ex : Exception) : Unit =
        guiActor ! Error(LocalDateTime.now, text, Some(ex))

    override def error(text : String) : Unit =
        guiActor ! Error(LocalDateTime.now, text, None)
} 
