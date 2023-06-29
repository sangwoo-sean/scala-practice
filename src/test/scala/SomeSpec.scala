import zio.Console.{printLine, readLine}
import zio._
import zio.test._

object SomeSpec extends ZIOSpecDefault {

  def spec =
    suite("http")(
      test("should be ok") {
        for {
          _ <- ZIO.unit
          _ = println("Hello")
        } yield assertTrue(true)
      }
    )

  def spec2 =
    suite("input")(
      test("should be ok") {
        for {
          _ <- printLine("Hello! What is your name?")
          name <- readLine
          _ <- printLine(s"Hello, $name, welcome to ZIO!")
        } yield assertTrue(true)
      },
    )

}

