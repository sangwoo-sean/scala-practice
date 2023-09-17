import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

import scala.util.{Failure, Success, Try}

object ErrorHandlingSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Try") (
      test("success") {
        val result = Try(2) match {
          case Failure(_) => 1
          case Success(value) => value
        }
        assertTrue(result == 2)
      },
      test("fail") {
        val result = Try(2/0) match {
          case Failure(e) =>
            println(e)
            1
          case Success(value) => value
        }
        assertTrue(result == 1)
      }
    ) +
      suite("Either") (
        test("right") {
          val result: Either[String, Int] = Right(2).map(_ + 1)

          assertTrue(result.getOrElse(0) == 3)
        },
        test("left") {
          val result: Either[String, Int] = Left("fail")

          assertTrue(result.isLeft && result.getOrElse(0) == 0)
        },
        suite("toOption") (
          test("from Left") {
            val result = Left("fail").toOption

            assertTrue(result.isEmpty)
          },
          test("from Right") {
            val result: Option[Int] = Right(3).toOption

            assertTrue(result.contains(3))
          }
        )
      )
}
