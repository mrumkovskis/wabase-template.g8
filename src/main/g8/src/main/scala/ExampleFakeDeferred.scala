package $package_name$

import org.apache.pekko.http.scaladsl.model.HttpResponse

import scala.concurrent.{ExecutionContext, Future}

object ExampleFakeDeferred {
  def exampleSlowRespond(millis: Long, response: HttpResponse)(implicit ec: ExecutionContext): Future[HttpResponse] = Future {
    Thread.sleep(millis)
    response
  }
}
