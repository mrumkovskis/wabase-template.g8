package $package_name$

import java.util.concurrent.atomic.AtomicReference

object ExampleJob {
  private val jobStatus = new AtomicReference[String]("Job not called")
  private val Did   = "Job did something"
  private val UnDid = "Job un-did something"

  def doJob = jobStatus.get match {
    case Did => jobStatus.set(UnDid)
    case _   => jobStatus.set(Did)
  }

  def status: String = jobStatus.get
}
