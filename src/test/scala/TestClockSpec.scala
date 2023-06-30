import zio.Clock.currentTime
import zio._
import zio.test._

import java.util.concurrent.TimeUnit

object TestClockSpec extends ZIOSpecDefault {

  def spec =
    suite("test clock")(
      test("make time go 1 minute") {
        for {
          startTime <- currentTime(TimeUnit.SECONDS)
          _         <- TestClock.adjust(30.seconds)
          endTime   <- currentTime(TimeUnit.SECONDS)
        } yield assertTrue((endTime - startTime) >= 29L && 31L >= (endTime - startTime))
      },
      test("control time") {
        for {
          promise <- Promise.make[Unit, Int]
          _ <- promise.succeed(1).delay(10.seconds).fork
          _ <- TestClock.adjust(10.seconds)
          readRef <- promise.await
        } yield assertTrue(readRef == 1)
      }
    )

}
