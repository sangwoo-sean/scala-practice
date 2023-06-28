import zhttp.http._
import zhttp.service.Server
import zio.{ZIO, ZIOAppDefault, ZLayer}

/** An http app that:
 *   - Accepts a `Request` and returns a `Response`
 *   - Does not fail
 *   - Does not use the environment
 */
object Main extends ZIOAppDefault {

  val prog = for {
    server <- ZIO.service[HttpServer]
    _ <- Server.start(8080, server.httpApp)
  } yield ()


  override def run = {
    prog.provide(HttpServer.layer)
  }

}

class HttpServer() {
  val httpApp = Http.collect[Request] {
    // GET /greet?name=:name
    case req@(Method.GET -> _ / "greet")
      if (req.url.queryParams.nonEmpty) =>
      Response.text(s"Hello ${req.url.queryParams("name").mkString(" and ")}!")

    // GET /greet
    case Method.GET -> _ / "greet" =>
      Response.text(s"Hello World!")

    // GET /greet/:name
    case Method.GET -> _ / "greet" / name =>
      Response.text(s"Hello $name!")

    case Method.GET -> _ / "fruits" / "a" =>
      Response.text("Apple")

    case Method.GET -> _ / "fruits" / "b" =>
      Response.text("banana")

    case Method.GET -> _ / "Apple" / int(count) =>
      Response.text(s"Apple: $count")

    case Method.GET -> _ / "scalatags" =>
      Response.html(Home.home.render)
//      Status.Ok()
  }
}


object HttpServer {
  val layer = ZLayer.succeed(new HttpServer())
}