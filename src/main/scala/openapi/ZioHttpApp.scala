package openapi

import zio._
import zio.http._
import zio.http.codec.HttpCodec.query
import zio.http.codec.{Doc, PathCodec}
import zio.http.endpoint._
import zio.http.endpoint.openapi.OpenAPIGen
import zio.schema.{DeriveSchema, Schema}

object ZioHttpApp extends ZIOAppDefault {

  sealed trait ErrorResponse

  case object ErrorResponse {
    implicit val schema: Schema[ErrorResponse] = DeriveSchema.gen
    case class Unexpected(message: String) extends ErrorResponse
  }

  val getEndpoint =
    Endpoint(Method.GET / "users" / int("userId") ?? Doc.p("""this is "userId" path parameter  """))
      .query(query("name").optional ?? Doc.p("""this is "name" query parameter """))
      .out[MyResponse]

  val getListEndpoint = Endpoint(Method.GET / "users")
    .out[Chunk[MyResponse]]
    .examplesOut("example" -> Chunk(MyResponse.example1, MyResponse.example2))

  val postEndpoint =
    Endpoint(Method.POST / "post")
      .in[MyRequest]
      .out[MyResponse]
      .outError[ErrorResponse](Status.BadRequest)
      .examplesIn("example1" -> MyRequest.example1, "example2" -> MyRequest.example2)
      .examplesOut("example1" -> MyResponse.example1, "example2" -> MyResponse.example2)

  val openAPI =
    OpenAPIGen.fromEndpoints(title = "Endpoint Example", version = "1.0", getEndpoint, getListEndpoint, postEndpoint)

  val docRoute = locally {
    SwaggerUI.routes(PathCodec("docs") / PathCodec("openapi"), openAPI)
  }

  val routes = Routes(
    getEndpoint.implement {
      Handler.fromFunctionZIO[(Int, Option[String])] {
        case (userId, Some(name)) =>
          ZIO.succeed(MyResponse(userId, name))
      }
    },
    getListEndpoint.implement {
      Handler.fromZIO(ZIO.succeed(Chunk(MyResponse.example1, MyResponse.example2)))
    },
    postEndpoint.implement {
      Handler.fromFunctionZIO(body =>
        for {
          _ <- ZIO.fail(ErrorResponse.Unexpected("test")).when(false)
        } yield MyResponse(body.myInt, body.myString)
      )
    }
  ) ++ docRoute

  val app = routes.toHttpApp @@ Middleware.requestLogging(logRequestBody = true, logResponseBody = true)

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .install(app)
      .flatMap(port => ZIO.logInfo(s"Listening on port $port") *> ZIO.never)
      .provide(Server.defaultWithPort(8082))

}
