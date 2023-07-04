import zio._

trait PersonRepository {
  def getAll: ZIO[Any, Any, List[Person]]

  def add(name: String, age: Int): ZIO[Any, Any, Long]

  def update(id: Long, name: String, age: Int): ZIO[Any, Any, Long]

  def delete(id: Long): ZIO[Any, Any, Long]
}