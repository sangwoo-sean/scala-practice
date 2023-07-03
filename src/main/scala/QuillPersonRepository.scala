import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

import java.sql.SQLException

class QuillPersonRepository(quill: Quill.Postgres[SnakeCase]) {
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

object QuillPersonRepository {
  def getPeople =
    ZIO.serviceWithZIO[QuillPersonRepository](_.getPeople)

  def add(name: String, age: Int): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.add(name, age))

  def update(id: Long, name: String, age: Int): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.update(id, name, age))

  def delete(id: Long): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.delete(id))

  val live = ZLayer.fromFunction(new QuillPersonRepository(_))
}