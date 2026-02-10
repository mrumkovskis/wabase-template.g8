package $package_name$

import org.wabase.client.WabaseHttpClient
import org.wabase.{AppQuerease, DefaultAppQuerease, WabaseServer}

import scala.language.reflectiveCalls

class RunningServer extends WabaseHttpClient {

  override protected def initQuerease: AppQuerease = DefaultAppQuerease

  override def login(username: String = null, password: String = null) = {
    ""
  }

  ServerState.synchronized {
    if (!ServerState.is_running) {
      WabaseServer.main(Array.empty)
      ServerState.is_running = true
    }
  }

  def unbind() = WabaseServer.unbind()
}

private object ServerState {
  var is_running = false
}
