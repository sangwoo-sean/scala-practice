import zio._

object ZIOLog extends ZIOAppDefault {

  val program =
    (for {
      _ <- ZIO.log("info")
      _ <- ZIO.logSpan("span") {
        ZIO.log("info in span start") *>
          ZIO.logSpan("inner-span") {
            ZIO.log("info in span 1").delay(1.second) *>
              ZIO.log("info in span 2").delay(1.second)
          } *>
          ZIO.log("info in span end").delay(1.second)
      }
    } yield ()) @@ ZIOAspect.annotated("annotated" -> "test")

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    program
}
