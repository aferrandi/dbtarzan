package dbtarzan.db.actor

import akka.actor.{Actor, ActorRef}
import dbtarzan.localization.Localization
import dbtarzan.messages.{CompositeIds, Composites}

class CompositeActor(  composites: Composites,
                       guiActor: ActorRef,
                       localization: Localization
                     )  extends Actor {
  var currentComposites : Composites = composites
  private def newComposites(composites: Composites): Unit = {
    currentComposites = composites
    guiActor ! CompositeIds(composites.composites.map(_.compositeId))
  }
  def receive = {
    case composites: Composites => newComposites(composites)
  }
}
