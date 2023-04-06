package dbtarzan.db.actor

import akka.actor.ActorRef
import dbtarzan.db.Composite
import dbtarzan.localization.Localization

class CompositeActor {
  class CompositeActor(
                       composite: Composite,
                       guiActor: ActorRef,
                       localization: Localization
                     )
}
