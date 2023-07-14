package Koreaexim

import zio._
import zio.http._
import zio.http.netty.NettyConfig
import zio.http.netty.client.NettyClientDriver

object KoreaeximClient extends ZIOAppDefault {
  val url = "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=3ltm76g7C0kGvXTEISglXjnFA7mEh6kC&searchdate=20230711&data=AP01"
  val prog = for {
    res <- Client.request(
      url = url,
      method = Method.GET
    )
    data <- res.body.asString
    _ <- Console.printLine(data)
  } yield()

  val run: ZIO[Any, Throwable, Unit] =
    prog.provide(
      ZLayer.succeed(ZClient.Config.default),
      Client.customized,
      NettyClientDriver.live,
      DnsResolver.default,
      ZLayer.succeed(NettyConfig.default)
    )
}
