import zio.test._
import zio._

object TestConsoleSpec extends ZIOSpecDefault{

  def spec =
    suite("testConsole") {
      test("test") {
        for {
          _ <- TestConsole.feedLines("Sangwoo", "27")
          _ <- Console.printLine("what is your name?")
          name <- Console.readLine
          _ <- Console.printLine("what is your age?")
          age <- Console.readLine.map(_.toInt)
        } yield {
          assertTrue(name == "Sangwoo") && assertTrue(age == 27)
        }
      }
    }

}
