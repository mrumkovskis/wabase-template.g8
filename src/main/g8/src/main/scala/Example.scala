import org.tresql._
import org.wabase.WabaseRequestContext

object Example {
  def fn(ctx: WabaseRequestContext) = {
    import ctx.wabase.tresqlResources
    tresql"""{demo(now())}"""
  }
}
