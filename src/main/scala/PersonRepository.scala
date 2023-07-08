import zio._

import java.sql.SQLException

trait PersonRepository {
  def getAll: ZIO[Any, Throwable, List[Person]]

  def get(id: Long): ZIO[Any, Throwable, Option[Person]]

  def add(name: String, age: Int): ZIO[Any, Throwable, Long]

  def update(id: Long, name: String, age: Int): ZIO[Any, Throwable, Long]

  def delete(id: Long): ZIO[Any, Throwable, Long]
}