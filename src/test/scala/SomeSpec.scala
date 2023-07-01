import zio.Console.{printLine, readLine}
import zio._
import zio.test._

import scala.runtime.Nothing$
import scala.util.Try

object SomeSpec extends ZIOSpecDefault {

  def spec =
    suite("http")(
      test("should be ok") {
        for {
          _ <- ZIO.unit
          _ = println("Hello")
        } yield assertTrue(true)
      }
    ) +
      suite("input")(
        test("should be ok") {
          for {
            _    <- TestConsole.feedLines("Sangwoo")
            _    <- printLine("Hello! What is your name?")
            name <- readLine
            _    <- printLine(s"Hello, $name, welcome to ZIO!")
          } yield assertTrue(true)
        }
      ) +
      suite("from option")(
        test("normal case") {
          for {
            value <- ZIO.fromOption(Some(2))
          } yield assertTrue(value == 2)
        },
        test("none is failure") {
          for {
            value <- ZIO.fromOption(None).isFailure
          } yield assertTrue(value)
        },
        test("some orElse") {
          for {
            value <- ZIO.fromOption(Some(1)).orElseSucceed(2)
          } yield assertTrue(value == 1)
        },
        test("none orElse") {
          for {
            value <- ZIO.fromOption(None).orElseSucceed(2)
          } yield assertTrue(value == 2)
        }
      ) +
      suite("from either")(
        test("normal case") {
          for {
            value <- ZIO.fromEither(Right(2))
          } yield assertTrue(value == 2)
        },
        test("normal case") {
          for {
            value <- ZIO.fromEither(Left(2)).isFailure
          } yield assertTrue(value)
        }
      ) +
      suite("from try")(
        test("try success") {
          for {
            value <- ZIO.fromTry(Try(4 / 2))
          } yield assertTrue(value == 2)
        },
        test("divided by zero is failure") {
          for {
            isFailure <- ZIO.fromTry(Try(4 / 0)).isFailure
          } yield assertTrue(isFailure)
        }
      )

}
