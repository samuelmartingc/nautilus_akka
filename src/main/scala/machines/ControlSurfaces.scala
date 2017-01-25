package machines

/**
  * Created by sam on 25/01/17.
  */
import akka.actor.{Actor, ActorRef}
object ControlSurfaces {
  case class StickBack(amount: Float)
  case class StickForward(amount: Float)
}

class ControlSurfaces(barometer: ActorRef) extends Actor {
  import ControlSurfaces._
  import Barometer._
  def receive = {
    // Nemo pulled the stick back by a certain
    // amount and we inform the Depth that we're climbing
    case StickBack(amount) =>
      barometer ! RateChange(amount)
    // Nemo pushes the stick forward and we
    // inform the Depth that we're descending
    case StickForward(amount) =>
      barometer ! RateChange(-1 * amount)
  }
}