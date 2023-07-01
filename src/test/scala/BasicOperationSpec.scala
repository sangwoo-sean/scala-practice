import zio._
import zio.test._

object BasicOperationSpec extends ZIOSpecDefault {

  def spec =
    suite("Basic Operation")(
      test("map") {
        for {
          result <- ZIO.succeed(1).map(_ * 2)
        } yield assertTrue(result == 2)
      },
      test("mapError") {
        for {
          isFailure <- ZIO.fail("fail").mapError(msg => new Exception(msg)).isFailure
        } yield assertTrue(isFailure)
      },
      test("flatmap") {
        for {
          _ <- TestConsole.feedLines("sangwoo")
          _ <- Console.readLine.flatMap(input => Console.printLine(s"You entered: $input"))
        } yield assertTrue(true)
      },
      test("zipping") {
        for {
          result <- ZIO.succeed("4").zip(ZIO.succeed(2))
        } yield assertTrue(result == ("4", 2))
      },
      test("zip right 1") {
        for {
          _      <- TestConsole.feedLines("sangwoo")
          result <- Console.printLine("what is your name?").zipRight(Console.readLine)
        } yield assertTrue(result == "sangwoo")
      },
      test("zip right 2") {
        for {
          _      <- TestConsole.feedLines("sangwoo")
          result <- Console.printLine("what is your name?") *> Console.readLine
        } yield assertTrue(result == "sangwoo")
      }
    )

}
