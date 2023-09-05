package sttp

import sttp.client4._
import sttp.client4.httpclient.zio.HttpClientZioBackend
import zio.{Schedule, Task, ZIO, ZIOAppDefault, durationInt}

object SttpPractice extends ZIOAppDefault {
  override def run: ZIO[Any, Throwable, Response[String]] =
    HttpClientZioBackend()
      .flatMap { backend =>

        val paramMap = Map(
          "limit" -> "10",
          "skip" -> "10",
          "select" -> "title, price"
        )

        basicRequest
          .get(uri"https://dummyjson.com/products?$paramMap")
          .response(asStringAlways)
          .send(backend)
          .tap(zio.Console.printLine(_))
          .ensuring(backend.close().ignore)
      }
}
