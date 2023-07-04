import zio._

import scala.collection.mutable

class InMemoryPersonRepository extends PersonRepository {

  def apply() = new InMemoryPersonRepository()

  private val peopleMap = mutable.HashMap(
    1L -> Person(1, "sang", 22),
    2L -> Person(2, "soon", 40),
    3L -> Person(3, "test", 30)
  )

  def getAll =
    ZIO.succeed(peopleMap.values.toList)

  def add(name: String, age: Int) = {
    val newId = peopleMap.size + 1
    peopleMap.addOne(newId, Person(newId, name, age))
    ZIO.succeed(newId)
  }

  def update(id: Long, name: String, age: Int) = {
    peopleMap.get(id) match {
      case Some(_) =>
        peopleMap.update(id, Person(id, name, age))
        ZIO.succeed(1L)
      case None =>
        ZIO.succeed(0L)
    }
  }

  def delete(id: Long) =
    peopleMap.get(id) match {
      case Some(_) =>
        peopleMap.remove(id)
        ZIO.succeed(1L)
      case None =>
        ZIO.succeed(0L)
    }
}

object InMemoryPersonRepository {
  def getPeople =
    ZIO.serviceWithZIO[InMemoryPersonRepository](_.getAll)

  def add(name: String, age: Int) =
    ZIO.serviceWithZIO[InMemoryPersonRepository](_.add(name, age))

  def update(id: Long, name: String, age: Int) =
    ZIO.serviceWithZIO[InMemoryPersonRepository](_.update(id, name, age))

  def delete(id: Long) =
    ZIO.serviceWithZIO[InMemoryPersonRepository](_.delete(id))

  val live = ZLayer.succeed(new InMemoryPersonRepository)
}