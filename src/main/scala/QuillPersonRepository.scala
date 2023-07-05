import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

import java.sql.SQLException

class QuillPersonRepository(quill: Quill.Postgres[SnakeCase]) extends PersonRepository {
  import quill._
  def getAll: ZIO[Any, SQLException, List[Person]] =
    run(query[Person])

  def get(id: Long): ZIO[Any, SQLException, Option[Person]] =
//    run(query[Person].filter(p => p.id == lift(id))) // 작동하지 않음
    ZIO.succeed(Option.empty)

  def add(name: String, age: Int): ZIO[Any, SQLException, Long] =
    run(query[Person].insert(_.name -> lift(name), _.age -> lift(age)))

  def update(id: Long, name: String, age: Int): ZIO[Any, SQLException, Long] =
    run(query[Person].filter(p => p.id == lift(id)).update(_.name -> lift(name), _.age -> lift(age)))

  def delete(id: Long): ZIO[Any, SQLException, Long] =
    run(query[Person].filter(p => p.id == lift(id)).delete)
}

object QuillPersonRepository {
  def getAll =
    ZIO.serviceWithZIO[QuillPersonRepository](_.getAll)

  def get(id: Long) =
    ZIO.serviceWithZIO[QuillPersonRepository](_.get(id))

  def add(name: String, age: Int): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.add(name, age))

  def update(id: Long, name: String, age: Int): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.update(id, name, age))

  def delete(id: Long): ZIO[QuillPersonRepository, SQLException, Long] =
    ZIO.serviceWithZIO[QuillPersonRepository](_.delete(id))

  val live = ZLayer.fromFunction(new QuillPersonRepository(_))
}
