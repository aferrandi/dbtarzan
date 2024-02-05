package dbtarzan.gui

import scala.language.postfixOps

import org.apache.pekko.actor.{ActorRef, ActorSystem}

class AppStopper(system: ActorSystem, guiActor: ActorRef, connectionsActor: ActorRef, logActor: ActorRef) {
  def closeApp(onExit: Runnable): Unit = {
    println("application exit")
    import org.apache.pekko.pattern.gracefulStop
    import scala.concurrent._
    import scala.concurrent.duration._
    import ExecutionContext.Implicits.global
    val stopAll = for {
      stopGui: Boolean <- gracefulStop(guiActor, 1 seconds)
      stopLog: Boolean <- gracefulStop(logActor, 1 seconds)
      stopConfig: Boolean <- gracefulStop(connectionsActor, 1 seconds)
    } yield stopGui && stopLog && stopConfig
    stopAll.foreach(_ => {
      system.terminate()
      println("shutdown")
      system.registerOnTermination(onExit)
    })
  }
}
