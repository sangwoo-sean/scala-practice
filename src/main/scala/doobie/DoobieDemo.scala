package doobie

import cats.effect.{ExitCode, IO, IOApp}
import doobie.implicits._

object DoobieDemo extends IOApp {

  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.ds.PGSimpleDataSource",
    "jdbc:postgresql:postgres",
    "postgres",
    "1111"
  )

  def findAllActorNames: IO[List[String]] = {
    val query  = sql"select person.name from person".query[String]
    val action = query.to[List]
    action.transact(xa)
  }

  override def run(args: List[String]): IO[ExitCode] =
    findAllActorNames.debug().as(ExitCode.Success)
}
