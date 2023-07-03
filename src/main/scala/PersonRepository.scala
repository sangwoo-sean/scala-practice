import zio.{ZIO, ZLayer}

import java.sql.SQLException

trait PersonRepository {
  def getAll: ZIO[Any, SQLException, List[Person]]

  def add(name: String, age: Int): ZIO[Any, SQLException, Long]

  def update(id: Long, name: String, age: Int): ZIO[Any, SQLException, Long]

  def delete(id: Long): ZIO[Any, SQLException, Long]
}