import zio._
import zio.test._

import java.io.IOException

object HelloWorld {
  def sayHello: ZIO[Any, IOException, Unit] =
    Console.printLine("Hello, World!")
}

import HelloWorld._

object HelloWorldSpec extends ZIOSpecDefault {

  def spec = {
    suite("HelloWorldSpec")(
      test("sayHello correctly displays output") {
        for {
          _      <- sayHello
          output <- TestConsole.output
        } yield assertTrue(output == Vector("Hello, World!\n"))
      },
      test("foo") {
        assertTrue(true)
      },
      test("foo bar") {
        assertTrue(true)
      },
      test("foo bar baz") {
        assertTrue(true)
      },
      test("test ZIO effects") {
        for {
          r <- Ref.make(0)
          _ <- r.update(_ + 1)
          v <- r.get
        } yield assertTrue(v == 1)
      }
    ) +
      suite("Second Suite")(
        test("some test") {
          assertTrue(true)
        }
      )
  }

}
