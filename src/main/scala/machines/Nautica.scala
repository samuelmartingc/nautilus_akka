package machines

/**
  * Created by sam on 25/01/17.
  */
import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration._
// The futures created by the ask syntax need an
// execution context on which to run, and we will use the
// default global instance for that context
import scala.concurrent.ExecutionContext.Implicits.global
object Nautica {
  // needed for '?' below
  implicit val timeout = Timeout(5.seconds)
  val system = ActorSystem("NautilusSimulation")
  val nautilus = system.actorOf(Props[Nautilus], "Nautilus")
  def main(args: Array[String]) {
    // Grab the controls
    val control = Await.result(
      (nautilus ? Nautilus.GiveMeControl).mapTo[ActorRef],
      5.seconds)
    // Takeoff!
    system.scheduler.scheduleOnce(200.millis) {
      control ! ControlSurfaces.StickBack(1f)
    }
    // Level out
    system.scheduler.scheduleOnce(1.seconds) {
      control ! ControlSurfaces.StickBack(0f)
    }
    // Climb
    system.scheduler.scheduleOnce(3.seconds) {
      control ! ControlSurfaces.StickBack(0.5f)
    }
    // Level out
    system.scheduler.scheduleOnce(4.seconds) {
      control ! ControlSurfaces.StickBack(0f)
    }
    // Shut down
    system.scheduler.scheduleOnce(5.seconds) {
      system.terminate()
    }
  }
}