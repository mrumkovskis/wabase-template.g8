package $package_name$

import org.mojoz.metadata.out.DdlGenerator
import org.tresql.*
import org.wabase.WabaseAppConfig.DefaultCp
import org.wabase.{DbAccess, DefaultAppQuerease, Loggable, TresqlResourcesConf, WabaseRequestContext}

object Example extends Loggable {
  def fn(ctx: WabaseRequestContext) = {
    import ctx.wabase.tresqlResources
    tresql"""{demo(now())}"""
  }

  def createHsqldbObjectsStatements = {
    val qe = DefaultAppQuerease
    Seq(
      "create sequence seq",
    ) ++
    DdlGenerator.hsqldb().schema(qe.tableMetadata.tableDefs).split(";").toList.map(_.trim).filter(_ != "")
  }

  def createHsqldbObjects =
    executeStatements(createHsqldbObjectsStatements*)

  def executeStatements(statements: String*): Unit = {
    val resourcesTemplate =
      TresqlResourcesConf.tresqlResourcesTemplate(TresqlResourcesConf.confs, DefaultAppQuerease.tresqlMetadata)
    DbAccess.newTransaction(DefaultCp, DefaultCp, resourcesTemplate) { implicit resources =>
      val conn = resources.conn
      val statement = conn.createStatement
      try statements foreach { s =>
        try {
          logger.info(s)
          statement.execute(s)
        } catch {
          case util.control.NonFatal(ex) =>
            throw new RuntimeException(s"Failed to execute sql statement:\n\$s", ex)
        }
      }
      finally statement.close()
    }
  }
}
