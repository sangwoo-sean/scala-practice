import io.getquill._
import io.getquill.jdbczio.Quill
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.{ZIO, ZIOAppDefault, ZLayer}

import java.sql.SQLException

case class Person(id: Long, name: String, age: Int)

object Person {
  implicit val encoder: JsonEncoder[Person] =
    DeriveJsonEncoder.gen[Person]
  implicit val decoder: JsonDecoder[Person] =
    DeriveJsonDecoder.gen[Person]
}

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople: ZIO[Any, SQLException, List[Person]] =
    run(query[Person])

  def add(name: String, age: Int): ZIO[Any, SQLException, Long] =
    run(query[Person].insert(_.name -> lift(name), _.age -> lift(age)))

  def update(id: Long, name: String, age: Int): ZIO[Any, SQLException, Long] =
    run(query[Person].filter(p => p.id == lift(id)).update(_.name -> lift(name), _.age -> lift(age)))

  def delete(id: Long): ZIO[Any, SQLException, Long] =
    run(query[Person].filter(p => p.id == lift(id)).delete)
}

object DataService {
  def getPeople =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  def add(name: String, age: Int): ZIO[DataService, SQLException, Long] =
    ZIO.serviceWithZIO[DataService](_.add(name, age))

  def update(id: Long, name: String, age: Int): ZIO[DataService, SQLException, Long] =
    ZIO.serviceWithZIO[DataService](_.update(id, name, age))

  def delete(id: Long): ZIO[DataService, SQLException, Long] =
    ZIO.serviceWithZIO[DataService](_.delete(id))

  val live = ZLayer.fromFunction(new DataService(_))
}

object DataServiceMain extends ZIOAppDefault {

  override def run =
    DataService
      .delete(5)
      .provide(
        DataService.live,
        Quill.Postgres.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
      .exitCode

}
