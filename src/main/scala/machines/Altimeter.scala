package machines

/**
  * Created by sam on 25/01/17.
  */

import akka.actor.{Props, Actor, ActorSystem, ActorLogging}
import scala.concurrent.duration._
object Altimeter {
  type Receive = PartialFunction[Any, Unit]
  case class RateChange(amount: Float)
  // Sent by the Altimeter at regular intervals
  case class AltitudeUpdate(altitude: Double)
}

class Altimeter extends Actor with ActorLogging with EventSource {
  import Altimeter._
  implicit val ec = context.dispatcher
  val ceiling = -3000

  val maxRateOfClimb = 1000
  var rateOfClimb = 0f
  var altitude = 0d
  // how much time has passed from last call
  var lastTick = System.currentTimeMillis
  // We need to periodically update our altitude
  val ticker = context.system.scheduler.schedule(
    100.millis, 100.millis, self, Tick)
  case object Tick

  def receive = eventSourceReceive orElse altimeterReceive
  def altimeterReceive :Receive = {
    case RateChange(amount) =>
      // Truncate the range of 'amount' to [-1, 1] before multiplying
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log info(s"Altimeter changed rate of climb to $rateOfClimb.")
    // Calculate a new altitude
    case Tick =>
      val tick = System.currentTimeMillis
      altitude = altitude + ((tick - lastTick) / 60000.0) *
        rateOfClimb
      lastTick = tick
      sendEvent(AltitudeUpdate(altitude))
  }
  // Kill our ticker when we stop
  override def postStop(): Unit = ticker.cancel
}