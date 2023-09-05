package sttp

import sttp.client4._
import sttp.client4.httpclient.zio.HttpClientZioBackend
import zio.{Schedule, Task, ZIO, ZIOAppDefault, durationInt}

object SttpPractice extends ZIOAppDefault {
  override def run: ZIO[Any, Throwable, Response[String]] =
    HttpClientZioBackend()
      .flatMap { backend =>
        val localhostRequest = basicRequest
          .get(uri"http://localhost:8080/api/people")
          .response(asStringAlways)


        val sendWithRetries: Task[Response[String]] = localhostRequest
          .send(backend)
          .either
          .repeat(
            Schedule.spaced(1.second) *>
              Schedule.recurs(10) *>
              Schedule.recurWhile(result => RetryWhen.Default(localhostRequest, result))
          )
          .absolve

        sendWithRetries.tap(zio.Console.printLine(_)).ensuring(backend.close().ignore)
      }
}
