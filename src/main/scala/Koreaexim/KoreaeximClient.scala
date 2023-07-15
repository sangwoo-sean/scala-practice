package Koreaexim

import zio._
import zio.http._
import zio.json.DecoderOps

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object KoreaeximClient extends ZIOAppDefault {
  private final val AUTH_KEY = "3ltm76g7C0kGvXTEISglXjnFA7mEh6kC"

  private def getUrl = {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val searchDate = LocalDate.now().minusDays(1).format(formatter)
    s"https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=$AUTH_KEY&searchdate=$searchDate&data=AP01"
  }

  val prog = for {
    _ <- ZIO.debug("request exchange rate")
    res  <- Client.request(url = getUrl, method = Method.GET)
    data <- res.body.asString
    dollarExchange = data.fromJson[List[KoreaeximResponse]].map(_.find(_.cur_unit == "USD"))
    dollarExchangeRate = dollarExchange match {
      case Left(_)      => 0
      case Right(value) => value.map(_.deal_bas_r.replaceAll(",", "").toDouble).getOrElse(0)
    }

    _ <- Console.printLine(dollarExchange)
  } yield dollarExchange

  val run =
    prog.provide(Client.default)
}
