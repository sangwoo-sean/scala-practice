import zhttp.http._
import zhttp.service._
import zio._

object MainApp extends ZIOAppDefault {

  val prog = for {
    server <- ZIO.service[MyServer]
    _ <- Server.start(8080, server.myApp)
  } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    prog.provide(MyServer.layer)
}

class MyServer() {
  val myApp = Http.collect[Request] {
    case Method.GET -> _ / "api" / "people" =>
      Response.text("people json")
  }
}

object MyServer {
  val layer = ZLayer.succeed(new MyServer())
}