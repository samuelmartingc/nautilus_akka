package machines

/**
  * Created by sam on 25/01/17.
  */
import akka.actor.{Props, Actor, ActorLogging}
object Nautilus {
  case object GiveMeControl
}

class Nautilus extends Actor with ActorLogging {
  import Barometer._
  import Nautilus._
  import EventSource._
  override def preStart() {
    barometer ! RegisterListener(self)
  }
  val barometer = context.actorOf(
    Props[Barometer], "Barometer")
  val controls = context.actorOf(
    Props(new ControlSurfaces(barometer)), "ControlSurfaces")
  def receive = {
    case DepthUpdate(barometer) =>
      log info(s"Depth is now: $barometer")
    case GiveMeControl =>
      log info("Nautilus giving control.")
      sender ! controls
  }
}