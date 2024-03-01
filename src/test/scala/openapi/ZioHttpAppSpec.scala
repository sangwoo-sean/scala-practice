package openapi

import zio.http._
import zio._
import zio.test._
import zio.json._

object ZioHttpAppSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ZioHttpAppSpec")(
      test("postEndpoint") {
        val route = ZioHttpApp.postEndpoint.implement {
          Handler.fromFunctionZIO(body => ZIO.succeed(MyResponse(body.myInt, body.myString, MyEnum.A)))
        }

        val request = Request.post(path = "/post", body = Body.fromString(MyRequest(1, "hello").toJson))

        for {
          response <- route.toHttpApp.runZIO(request)
          body     <- response.body.asString.debug("body")
        } yield assertCompletes
      },
      test("validate schema") {
        val schema = zio.schema.Schema[MyRequest]

        val codec = zio.schema.codec.JsonCodec.jsonCodec(schema)
        val req = codec
          .decodeJson("""{
            |    "myInt": 1,
            |    "myString": "hellow"
            |}""".stripMargin)
          .toOption
          .get
        schema.validate(req)

        assertCompletes
      }
    )

}
