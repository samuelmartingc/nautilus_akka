package machines

/**
  * Created by sam on 25/01/17.
  */

import akka.actor.{Props, Actor, ActorSystem, ActorLogging}
import scala.concurrent.duration._
object Barometer {
  type Receive = PartialFunction[Any, Unit]
  case class RateChange(amount: Float)
  // Sent by the Barometer at regular intervals
  case class DepthUpdate(depth: Double)
}

class Barometer extends Actor with ActorLogging with EventSource {
  import Barometer._
  implicit val ec = context.dispatcher
  val ceiling = 4000

  val maxRateOfClimb = 1000
  var rateOfClimb = 0f
  var depth = 0d
  // how much time has passed from last call
  var lastTick = System.currentTimeMillis
  // We need to periodically update our depth
  val ticker = context.system.scheduler.schedule(
    100.millis, 100.millis, self, Tick)
  case object Tick

  def receive = eventSourceReceive orElse barometerReceive
  def barometerReceive :Receive = {
    case RateChange(amount) =>
      // Truncate the range of 'amount' to [-1, 1] before multiplying
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log info(s"Depth changed rate of climb to $rateOfClimb.")
    // Calculate a new depth
    case Tick =>
      val tick = System.currentTimeMillis
      depth = depth + ((tick - lastTick) / 60000.0) *
        rateOfClimb
      lastTick = tick
      sendEvent(DepthUpdate(depth))
  }
  // Kill our ticker when we stop
  override def postStop(): Unit = ticker.cancel
}