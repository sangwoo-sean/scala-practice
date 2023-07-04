import zhttp.http._
import zio.json.EncoderOps
import zio.{ZIO, ZLayer}

class PersonEndPoint(dataService: InMemoryPersonRepository) {

  val endpoint = Http.collectZIO[Request] {
    case Method.GET -> _ / "api" / "people" =>
      dataService.getAll.map(res => Response.json(res.toJson))
    case Method.POST -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
    case Method.PATCH -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
    case Method.DELETE -> _ / "api" / "people" =>
      ZIO.succeed(Response.text("people json"))
  }

}

object PersonEndPoint {
  val live: ZLayer[InMemoryPersonRepository, Nothing, PersonEndPoint] =
    ZLayer.fromFunction(new PersonEndPoint(_))
}
