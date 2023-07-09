import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateErrorResult
import zio.{ZIO, ZLayer}

import javax.sql.DataSource

class DbMigrator(dataSource: DataSource) {

  def migrate: ZIO[Any, Throwable, Unit] = {
    ZIO
      .attempt(
        Flyway
          .configure()
          .dataSource(dataSource)
          .load()
          .migrate()
      )
      .flatMap {
        case r: MigrateErrorResult => ZIO.fail(DbMigrationFailed(r.error.message, r.error.stackTrace))
        case _ => ZIO.succeed(())
      }
      .onError(cause => ZIO.logErrorCause("Database migration has failed", cause))
  }
}

case class DbMigrationFailed(msg: String, stackTrace: String) extends RuntimeException(s"$msg\n$stackTrace")

object DbMigrator {
  def live: ZLayer[DataSource, Nothing, DbMigrator] = ZLayer.fromFunction(new DbMigrator(_))
}
