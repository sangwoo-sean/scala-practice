import zio._
import zio.http._
import zio.http.codec.HttpCodec.query
import zio.http.endpoint._
import zio.http.endpoint.openapi.OpenAPIGen

object ZioHttpApp extends ZIOAppDefault {

  val getEndpoint =
    Endpoint(Method.GET / "users" / int("userId") / "posts" / int("postId"))
      .query(query("name"))
      .out[List[String]]

//  val openAPI = OpenAPIGen.fromEndpoints(title = "Endpoint Example", version = "1.0", getEndpoint)
//  val docRoute = SwaggerUI.routes("docs" / "openapi", openAPI)

  val routes = Routes(
    getEndpoint.implement {
      Handler.fromFunctionZIO[(Int, Int, String)] {
        case (userId, postId, name) =>
          ZIO.succeed(List(s"$userId", s"$postId", name))
      }
    }
  )

  val app = routes.toHttpApp
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.serve(app).provide(Server.defaultWithPort(8082))
}
