package openapi

import zio.http._
import zio._
import zio.http.endpoint.Endpoint
import zio.test._
import zio.json._

object ZioHttpAppSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ZioHttpAppSpec")(
      test("test") {
        val endpoint = Endpoint(Method.POST / "test")
          .in[MyRequest]
          .out[String]

        val route = endpoint.implement {
          Handler.fromFunctionZIO(body => ZIO.succeed(MyResponse(body.myInt, body.myString).toJson))
        }

        val app = route.toHttpApp

        val request = Request.post(path = "/test", body = Body.fromString(MyRequest(1, "hello").toJson))

        for {
          response <- app.runZIO(request)
          body     <- response.body.asString.debug("body")
        } yield assertCompletes
      }
    )

}
