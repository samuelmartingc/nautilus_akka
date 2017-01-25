package machines

/**
  * Created by sam on 25/01/17.
  */
import akka.actor.{Actor, ActorRef}
object EventSource {
  case class RegisterListener(listener: ActorRef)
  case class UnregisterListener(listener: ActorRef)
}
trait EventSource { this: Actor =>
  import EventSource._
  var listeners = Vector.empty[ActorRef]
  // Sends the event to all of our listeners
  def sendEvent[T](event: T): Unit = listeners foreach {
    _ ! event
  }
  def eventSourceReceive: Receive = {
    case RegisterListener(listener) =>
      listeners = listeners :+ listener
    case UnregisterListener(listener) =>
      listeners = listeners filter { _ != listener }
  }
}