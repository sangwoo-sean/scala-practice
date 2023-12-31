import zhttp.http._
import zio.json._
import zio._

class PersonEndPoint(personRepository: PersonRepository) {

  val endpoint = Http.collectZIO[Request] {
    case Method.GET -> _ / "api" / "people" =>
      personRepository.getAll.map(res => Response.json(res.toJson))
    case Method.GET -> _ / "api" / "people" / id =>
      personRepository.get(id.toLong).map(res => Response.json(res.toJson))
    case req @ Method.POST -> _ / "api" / "people" =>
      for {
        person <- req.bodyAsString.map(_.fromJson[PersonRequest])

        res <- person match {
          case Right(p) =>
            personRepository.add(p.name, p.age) *>
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
  val live: ZLayer[PersonRepository, Nothing, PersonEndPoint] =
    ZLayer.fromFunction(new PersonEndPoint(_))
}
