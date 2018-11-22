package dbtarzan.gui

import akka.actor.{ ActorSystem, Props, ActorRef, Actor }

/* crates and keeps track of the main actors in this application */
class ActorHandler (guiActorSupplier : () =>  Actor, configActorSupplier: ActorRef => Actor) {
  private val system = ActorSystem("Sys")
  val guiActor : ActorRef = system.actorOf(Props(guiActorSupplier()).withDispatcher("my-pinned-dispatcher"), "guiWorker")
  val connectionsActor : ActorRef = system.actorOf(Props(configActorSupplier(guiActor)).withDispatcher("my-pinned-dispatcher"), "configWorker")

  /* first we close the dbWorker actors. Then the config and gui actors. Then we stop the actor system and we check that there are no more actors. 
      Once this is done, we close JavaF (the GUI)
  */
  def closeApp(onExit : Runnable) : Unit = {
    println("application exit")
    import akka.pattern.gracefulStop
    import scala.concurrent._
    import scala.concurrent.duration._
    import ExecutionContext.Implicits.global
    val stopAll = for {
      stopGui : Boolean <- gracefulStop(guiActor, 1 seconds)
      stopConfig : Boolean <- gracefulStop(connectionsActor, 1 seconds)
    } yield stopGui && stopConfig
    stopAll.foreach(x => { 
      system.terminate()
      println("shutdown")
      system.registerOnTermination(onExit)
    })
  }
}
