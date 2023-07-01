import zio._
import zio.test._

object HandlingErrorSpec extends ZIOSpecDefault {
  def spec =
    suite("handling errors") (
      test("either") {
        for {
          result <- ZIO.fail("no way").either
        } yield assertTrue(result.isLeft)
      },
      test("error fold") {
        for {
          result <- ZIO.fail(1).fold(
            failure = error => error,
            success = data => data
          )
        } yield assertTrue(result == 1)
      },
      test("error foldZIO") {
        for {
          result <- ZIO.fail(1).foldZIO(
            failure = error => ZIO.succeed(error),
            success = data => ZIO.succeed(data)
          )
        } yield assertTrue(result == 1)
      },
//      test("error retry") {
//        for {
//          result <- func.retry(Schedule.recurs(3))
//        } yield assertTrue(result == 1)
//      } // 재시도 되지 않음
    )

  var count = 0
  private def func = {
    println(s"executing func... count: $count")
    if (count >= 2) {
      println("func succeeded")
      ZIO.succeed(1)
    }
    else {
      println("func failed")
      count += 1
      ZIO.fail(2)
    }
  }

}
