import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zhttp.http._
import zhttp.service._
import zio._
import zio.json._

object Main extends ZIOAppDefault {

  val port = 8080

  val prog = for {
    server <- ZIO.service[MyServer]
    _ <- Server.start(port, server.myApp)
  } yield ()

  override def run: ZIO[Any, Throwable, Unit] =
    prog.provide(MyServer.live,
      DataService.live,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      Quill.DataSource.fromPrefix("myDatabaseConfig"))
}

class MyServer(dataService: DataService) {
  val myApp = Http.collectZIO[Request] {
    case Method.GET -> _ / "api" / "people" =>
      dataService.getPeople.map(res => Response.json(res.toJson))
    case Method.POST -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
    case Method.PATCH -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
    case Method.DELETE -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
  }
}

object MyServer {
  val live: ZLayer[DataService, Nothing, MyServer] =
    ZLayer.fromFunction(new MyServer(_))
}