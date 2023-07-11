import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zhttp.service._
import zio._
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[Any, Any, Unit] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  val port = 8080

  val prog: ZIO[PersonEndPoint with DbMigrator, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Start")
    _ <- ZIO.logInfo("It's Scheduling!").repeat(Schedule.fixed(Duration.fromSeconds(60)).forever).fork
    migrator <- ZIO.service[DbMigrator]
    _ <- migrator.migrate
    personEndPoint <- ZIO.service[PersonEndPoint]
    _              <- Server.start(port, personEndPoint.endpoint)
    _ <- ZIO.logInfo("Done")
  } yield ()

  override def run: ZIO[Any, Throwable, Unit] =
    prog.provide(
      DbMigrator.live,
      PersonEndPoint.live,
      QuillPersonRepository.live,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      Quill.DataSource.fromPrefix("myDatabaseConfig")
    )

}
