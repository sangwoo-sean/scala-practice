import zio.Console.printLine
import zio.{durationInt, Chunk, Random, Schedule, Scope, ZIO}
import zio.stream.ZStream
import zio.test.{assertTrue, Spec, TestAspect, TestEnvironment, ZIOSpecDefault}

object ZStreamSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Create ZStream")(
      test("apply") {
        for {
          res <- ZStream(1, 2, 3)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 3)
      },
      test("unit - contains a single Unit value") {
        for {
          res <- ZStream.unit.runCount
        } yield assertTrue(res == 1)
      },
      test("never - produces no value or fails with an error") {
        for {
          res <- ZStream.never // this stream never ends
            .timeoutTo(1.seconds)(ZStream.unit) // alternative stream
            .runCount
        } yield assertTrue(res == 1)
      },
      test("repeat - repeats using the specified schedule") {
        for {
          res <- ZStream(1)
            .repeat(Schedule.recurs(3)) // 1, (1, 1 ,1)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 4)
      },
      test("iterate") {
        for {
          res <- ZStream
            .iterate(1)(_ + 1) // 1, 2, 3, ...
            .tap(printLine(_)) // 1, 2, 3, 4
            .takeWhile(_ < 4)  // 1, 2, 3
            .map(x => x)
            .runCount
        } yield assertTrue(res == 3)
      },
      test("range") {
        for {
          res <- ZStream
            .range(1, 5) // 1, 2, 3, 4
            .runCount
        } yield assertTrue(res == 4)
      },
      test("success") {
        for {
          res <- ZStream.succeed(1).runCount
        } yield assertTrue(res == 1)
      },
      test("fail") {
        for {
          res <- ZStream
            .fail("Nope")
            .tapError { e =>
              println(e)
              ZIO.fail(e)
            }
            .orElse(ZStream(1, 2, 3))
            .runCount
        } yield assertTrue(res == 3)
      },
      test("from chunks") {
        for {
          res <- ZStream
            .fromChunks(Chunk(1, 2, 3), Chunk(4, 5, 6))
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 6)
      },
      test("from ZIO") {
        for {
          res <- ZStream
            .fromZIO(Random.nextInt)
            .tap(printLine(_))
            .runCount
        } yield assertTrue(res == 1)
      }
    ) @@ TestAspect.withLiveClock +
      suite("ZStream Operations")(
        test("???") {
          for {
            _ <- ZIO.unit
          } yield assertTrue(true)
        }
      ) +
      suite("ZStream Error Handling")(
        test("???") {
          for {
            _ <- ZIO.unit
          } yield assertTrue(true)
        }
      )

}
