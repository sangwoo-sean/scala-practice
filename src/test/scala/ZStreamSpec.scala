import zio.Console.printLine
import zio.{durationInt, Chunk, Random, Schedule, Scope, ZIO}
import zio.stream.{ZSink, ZStream}
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
        test("take") {
          for {
            _ <- ZIO.unit
            stream = ZStream.iterate(0)(_ + 1)

            s1 <- stream.take(3).runCollect
            s2 <- stream.takeWhile(_ < 3).runCollect
            s3 <- stream.takeUntil(_ == 3).runCollect
            s4 <- stream.takeUntil(_ == 3).takeRight(2).runCollect
          } yield assertTrue(
            s1 == Chunk(0, 1, 2) &&
              s2 == Chunk(0, 1, 2) &&
              s3 == Chunk(0, 1, 2, 3) &&
              s4 == Chunk(2, 3)
          )
        },
        test("map") {
          for {
            res <- ZStream.fromIterable(0 to 5)
              .map(_.toString)
              .runCollect
          } yield assertTrue(res == Chunk("0", "1", "2", "3", "4", "5"))
        },
        test("mapAccum") {
          for {
            res <- ZStream
              .fromIterable(0 to 5)
              .mapAccum(0)((acc, next) => (acc + next, acc + next))
              .runCollect
          } yield assertTrue(res == Chunk(0, 1, 3, 6, 10, 15))
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
