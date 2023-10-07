import zio._
import zio.test._

import java.util.UUID
import scala.util.Try

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
      },
      test("mapAccum") {
        Chunk.from(1 to 10).mapAccum(UUID.randomUUID()) {
          case (prev, value) =>
            println(s"previous UUID: $prev")
            val newId = UUID.randomUUID()
            println(s"new      UUID: $newId")
            (newId, value)
        }

        assertTrue(true)
      },
      test("Map withDefaultValue") {
        val prevMap = Map(1 -> "a", 2 -> "b", 3 -> "c")

        val applyContainingValue = prevMap(3)
        val getContainingValue   = prevMap.get(3)

        val tryApplyNoMappingValue = Try(prevMap(4))
        val getNoMappingValue      = prevMap.get(5)

        val withDefaultValueMap = prevMap.withDefaultValue("empty")

        val applyNoMappingValue = withDefaultValueMap(4)
        val getNoMappingValue2  = withDefaultValueMap.get(5)
        assertTrue(
          applyContainingValue == "c" &&
            getContainingValue.contains("c") &&
            tryApplyNoMappingValue.isFailure &&
            getNoMappingValue.isEmpty &&
            withDefaultValueMap.size == 3 &&
            applyNoMappingValue == "empty" &&
            getNoMappingValue2.isEmpty
        )
      },
      test("currying") {
        def funcA(a: Int, b: Int) = a + b
        def funcB(a: Int)(b: Int) = a + b

        // 같은 인자를 주고, 내부적으로도 같은 함수(funcD) 를 사용하지만 결과를 다르게 할 수 있다.
        // 내부에서 비슷한 로직을 사용하고 인자도 같지만, 일부분 다른 로직을 사용할 때 고려해볼 수 있다.
        def funcC1(a: Int)        = funcD(a, 1)
        def funcC2(a: Int)        = funcD(a, 2)
        def funcD(a: Int, b: Int) = a + b

        assertTrue(
          funcA(1, 2) == 3 &&
            funcB(1)(2) == 3 &&
            funcC1(1) == 2 &&
            funcC2(1) == 3
        )
      }
    )

}
