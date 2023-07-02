import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZIOAppDefault, ZLayer}

case class Person(id: Long, name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople = run(query[Person])
  def add       = run(query[Person].insert(_.name -> lift("json"), _.age -> lift(30)))
  def update    = run(query[Person].filter(p => p.id == lift(3)).update(_.age -> lift(35)))
}

object DataService {
  def getPeople =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  def add =
    ZIO.serviceWithZIO[DataService](_.add)

  def update =
    ZIO.serviceWithZIO[DataService](_.update)

  val live = ZLayer.fromFunction(new DataService(_))
}

object DataServiceMain extends ZIOAppDefault {

  override def run =
    DataService.update
      .provide(
        DataService.live,
        Quill.Postgres.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
      .exitCode

}
