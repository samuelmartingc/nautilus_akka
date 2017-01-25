package machines

/**
  * Created by sam on 25/01/17.
  */
import akka.actor.{Props, Actor, ActorLogging}
object Nautilus {
  case object GiveMeControl
}

class Nautilus extends Actor with ActorLogging {
  import Altimeter._
  import Nautilus._
  import EventSource._
  override def preStart() {
    altimeter ! RegisterListener(self)
  }
  val altimeter = context.actorOf(
    Props[Altimeter], "Altimeter")
  val controls = context.actorOf(
    Props(new ControlSurfaces(altimeter)), "ControlSurfaces")
  def receive = {
    case AltitudeUpdate(altitude) =>
      log info(s"Altitude is now: $altitude")
    case GiveMeControl =>
      log info("Nautilus giving control.")
      sender ! controls
  }
}