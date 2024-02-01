import zio.stream._
import zio.{Chunk, Scope, ZIO}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object ZSinkSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ZSink")(
      test("head") {
        for {
          _ <- ZIO.unit
          sink: ZSink[Any, Nothing, Int, Int, Option[Int]] = ZSink.head[Int]
          head <- ZStream(1, 2, 3, 4).run(sink)
        } yield assertTrue(head.contains(1))
      },
      test("last") {
        for {
          _ <- ZIO.unit
          sink: ZSink[Any, Nothing, Int, Int, Option[Int]] = ZSink.last[Int]
          last <- ZStream(1, 2, 3, 4).run(sink)
        } yield assertTrue(last.contains(4))
      },
      test("count") {
        for {
          _ <- ZIO.unit
          sink: ZSink[Any, Nothing, Int, Nothing, Long] = ZSink.count
          count <- ZStream(1, 2, 3, 4, 5).run(sink)
        } yield assertTrue(count == 5)
      },
      test("sum") {
        for {
          _ <- ZIO.unit
          sink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.sum[Int]
          sum <- ZStream(1, 2, 3, 4, 5).run(sink)
        } yield assertTrue(sum == 15)
      },
      test("leftover") {
        for {
          _ <- ZIO.unit
          sink: ZSink[Any, Nothing, Int, Nothing, (Option[Int], Chunk[Int])] = ZSink.head[Int].collectLeftover
          result <- ZStream(1, 2, 3, 4).run(sink)
          (head, leftover) = result
        } yield assertTrue(head.contains(1) && leftover.size == 3)
      },
    )

}
