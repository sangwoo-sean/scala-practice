import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZIOAppDefault, ZLayer}

case class Person(id: Long, name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople =
    run(query[Person])

  def add(name: String, age: Int) =
    run(query[Person].insert(_.name -> lift(name), _.age -> lift(age)))

  def update(id: Long, name: String, age: Int) =
    run(query[Person].filter(p => p.id == lift(id)).update(_.name -> lift(name), _.age -> lift(age)))

  def delete(id: Long) =
    run(query[Person].filter(p => p.id == lift(id)).delete)
}

object DataService {
  def getPeople =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  def add(name: String, age: Int) =
    ZIO.serviceWithZIO[DataService](_.add(name, age))

  def update(id: Long, name: String, age: Int) =
    ZIO.serviceWithZIO[DataService](_.update(id, name, age))

  def delete(id: Long) =
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
