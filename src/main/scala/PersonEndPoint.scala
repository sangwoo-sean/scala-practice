import zhttp.http._
import zio.json.{DecoderOps, EncoderOps}
import zio.{ZIO, ZLayer}

class PersonEndPoint(dataService: InMemoryPersonRepository) {

  val endpoint = Http.collectZIO[Request] {
    case Method.GET -> _ / "api" / "people" =>
      dataService.getAll.map(res => Response.json(res.toJson))
    case req @ Method.POST -> _ / "api" / "people" =>
      for {
        person <- req.bodyAsString.map(_.fromJson[PersonRequest])
        _ <- ZIO.logInfo(s"person: $person")
        res <- person match {
          case Right(p) =>
            dataService.add(p.name, p.age) *>
              ZIO.succeed(Response.ok)
          case _ => ZIO.succeed(Response.status(Status.BadRequest))
        }
      } yield res
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
