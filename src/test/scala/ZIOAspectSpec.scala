import zio._
import zio.internal.ExecutionMetrics
import zio.test.{Spec, TestAspect, TestClock, TestEnvironment, ZIOSpecDefault, assertTrue}

object ZIOAspectSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("test aspect")(
      test("annotated with key and value") {
        val sample = "test sample"
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.annotated(
          "key"    -> "value",
          "sample" -> sample
        )
      },
      test("annotated with annotations") {
        val sample = "test sample"
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.annotated(
          ("key", "value"),
          ("sample", sample)
        )
      },
      test("debug") {
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.debug
      },
      test("debug with prefix") {
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.debug("prefix")
      },
      test("logged") {
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.logged
      },
      test("logged with label") {
        (for {
          _ <- ZIO.unit
        } yield assertTrue(true)) @@ ZIOAspect.logged("label")
      },
      test("parallel") {
        // 10개의 작업을 병렬로 실행하지만, 3개의 fiber만 사용하도록 제한한다.
        (for {
          _ <- ZIO.foreachParDiscard((1 to 10).toList)(x =>
            ZIO
              .logInfo(x.toString)
              .delay(Duration.fromSeconds(1))
          )
        } yield assertTrue(true)) @@ ZIOAspect.parallel(3)
      } @@ TestAspect.withLiveClock, // test aspect 로 테스트시에도 실제 시간을 사용할 수 있게 해줌
      test("parallel unbounded") {
        // 10개의 작업을 병렬로, 이용 가능한 최대 fiber 개수만큼 사용한다.
        // fiber 는 heap memory 를 가용한 만큼 생길 수 있다. (상당히 많이 가능)
        (for {
          _ <- ZIO.foreachParDiscard((1 to 10).toList)(x =>
            ZIO
              .logInfo(x.toString)
              .delay(Duration.fromSeconds(1))
          )
        } yield assertTrue(true)) @@ ZIOAspect.parallelUnbounded
      } @@ TestAspect.withLiveClock // test aspect 로 테스트시에도 실제 시간을 사용할 수 있게 해줌
    )

}
