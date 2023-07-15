package Koreaexim

import zhttp.http._
import zio._

class KoreaeximEndpoints() {

  val endpoint = Http.collectZIO[Request] {
    case Method.GET -> _ / "api" / "exchange" =>
      Koreaexim.KoreaeximClient.run.map {
        case Left(_) => Response.status(Status.InternalServerError)
        case Right(value) => Response.json(value.get.toString)
      }
  }
}

object KoreaeximEndpoints {
  val live: ZLayer[Any, Nothing, KoreaeximEndpoints] =
    ZLayer.succeed(new KoreaeximEndpoints)
}
