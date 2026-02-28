package $package_name$

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.wabase.{EventMessage, ServerNotifications}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object ExampleServerEvents {

  def publishEvent(topic: String, value: String) = {
    ServerNotifications.publish { _.publish(EventMessage(topic, value)) }
  }

  def subscribeToExampleEvents(topic: String, as: ActorSystem, req: HttpRequest): Future[HttpResponse] = {
    // Using two independent execution contexts in this example
    // so that events are published after response
    val ec1: ExecutionContext = as.dispatcher
    val ec2: ExecutionContext = ExecutionContext.global

    val response =
      ServerNotifications.subscribeToEventsAndListen(b => a => b.subscribe(a, topic), _ => ())(as, req)
    response.onComplete {
      case Success(_) => Future(publishExampleEvents(topic))(using ec2)
      case _ =>
    }(using ec1)
    response
  }

  def publishExampleEvents(topic: String): Unit = {
    Thread.sleep(150)
    publishEvent(topic, "event-1")
    Thread.sleep(150)
    publishEvent(topic, "event-2")
  }
}
