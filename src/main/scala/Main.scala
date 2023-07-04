import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zhttp.service._
import zio._

object Main extends ZIOAppDefault {

  val port = 8080

  val prog = for {
    personEndPoint <- ZIO.service[PersonEndPoint]
    _              <- Server.start(port, personEndPoint.endpoint)
  } yield ()

  override def run: ZIO[Any, Throwable, Unit] =
    prog.provide(
      PersonEndPoint.live,
      InMemoryPersonRepository.live,
//      QuillPersonRepository.live,
//      Quill.Postgres.fromNamingStrategy(SnakeCase),
//      Quill.DataSource.fromPrefix("myDatabaseConfig")
    )

}
