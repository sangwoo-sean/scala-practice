import zio._
import zio.test._

import java.io.IOException

object HelloWorld {
  def sayHello: ZIO[Any, IOException, Unit] =
    Console.printLine("Hello, World!")
}

import HelloWorld._

object HelloWorldSpec extends ZIOSpecDefault {
  def spec = suite("HelloWorldSpec")(
    test("sayHello correctly displays output") {
      for {
        _ <- sayHello
        output <- TestConsole.output
      } yield assertTrue(output == Vector("Hello, World!\n"))
    }
  )
}
