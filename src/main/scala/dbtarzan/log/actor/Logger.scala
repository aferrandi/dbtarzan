package dbtarzan.log.actor

import dbtarzan.messages.{Debug, Error, Info, TLogger, Warning}
import org.apache.pekko.actor.ActorRef

import java.time.LocalDateTime

/* acts as a classic "Logger" class, but sends the messages to the guiActor */
class Logger(logActor : ActorRef) extends TLogger {
    override def debug(text : String) : Unit =
        logActor ! Debug(LocalDateTime.now, text)

    override def info(text : String) : Unit =
        logActor ! Info(LocalDateTime.now, text)

    override def warning(text : String) : Unit =
        logActor ! Warning(LocalDateTime.now, text)

    override def error(text : String, ex : Exception) : Unit =
        logActor ! Error(LocalDateTime.now, text, Some(ex))

    override def error(text : String) : Unit =
        logActor ! Error(LocalDateTime.now, text, None)
} 
