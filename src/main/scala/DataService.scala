import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZIOAppDefault, ZLayer}

case class Person(name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople = run(query[Person])
}

object DataService {
  def getPeople =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  val live = ZLayer.fromFunction(new DataService(_))
}

object DataServiceMain extends ZIOAppDefault {

  override def run =
    DataService.getPeople
      .provide(
        DataService.live,
        Quill.Postgres.fromNamingStrategy(SnakeCase),
        Quill.DataSource.fromPrefix("myDatabaseConfig")
      )
      .debug("Results")
      .exitCode

}
