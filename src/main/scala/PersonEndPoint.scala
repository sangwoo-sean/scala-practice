import zhttp.http._
import zio.json.EncoderOps
import zio.{ZIO, ZLayer}

class PersonEndPoint(dataService: QuillPersonRepository) {

  val endpoint = Http.collectZIO[Request] {
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

object PersonEndPoint {
  val live: ZLayer[QuillPersonRepository, Nothing, PersonEndPoint] =
    ZLayer.fromFunction(new PersonEndPoint(_))
}
