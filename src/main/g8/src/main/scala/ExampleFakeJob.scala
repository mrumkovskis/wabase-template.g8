package $package_name$

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.{ExecutionContext, Future}

object ExampleFakeJob {
  private val jobStatus = new AtomicReference[String]("Job not called")
  private val Did   = "Job did something"
  private val UnDid = "Job un-did something"

  def doJob(implicit ec: ExecutionContext) = Future(jobStatus.get match {
    case Did => jobStatus.set(UnDid)
    case _   => jobStatus.set(Did)
  })

  def status: String = jobStatus.get
}
