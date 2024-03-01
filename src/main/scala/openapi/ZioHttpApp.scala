package openapi

import zio._
import zio.http._
import zio.http.codec.HttpCodec.query
import zio.http.codec.{Doc, PathCodec}
import zio.http.endpoint._
import zio.http.endpoint.openapi.OpenAPIGen

object ZioHttpApp extends ZIOAppDefault {

  val getEndpoint =
    Endpoint(Method.GET / "users" / int("userId") ?? Doc.p("""this is "userId" path parameter  """))
      .query(query("name").optional ?? Doc.p("""this is "name" query parameter """))
      .out[List[String]]

  val postEndpoint =
    Endpoint(Method.POST / "post")
      .in[String]
      .out[MyResponse]

  val openAPI = OpenAPIGen.fromEndpoints(title = "Endpoint Example", version = "1.0", getEndpoint, postEndpoint)

  val docRoute = locally {
    SwaggerUI.routes(PathCodec("docs") / PathCodec("openapi"), openAPI)
  }

  val routes = Routes(
    getEndpoint.implement {
      Handler.fromFunctionZIO[(Int, Option[String])] {
        case (userId, Some(name)) =>
          ZIO.succeed(List(s"$userId", name))
      }
    },
    postEndpoint.implement {
      Handler.fromFunctionZIO(body => ZIO.succeed(MyResponse(1, "str")))
    }
  ) ++ docRoute

  val app = routes.toHttpApp

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .install(app)
      .flatMap(port => ZIO.logInfo(s"Listening on port $port") *> ZIO.never)
      .provide(Server.defaultWithPort(8082))

}
