import zio._
import zio.json.DecoderOps
import zio.test._

import java.time.format.DateTimeFormatter

import java.time.LocalDate

object KoreaeximSpec extends ZIOSpecDefault {

  final val res_sample =
    """
      |[
      |    {
      |        "result": 1,
      |        "cur_unit": "AED",
      |        "ttb": "351.41",
      |        "tts": "358.5",
      |        "deal_bas_r": "354.96",
      |        "bkpr": "354",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "354",
      |        "kftc_deal_bas_r": "354.96",
      |        "cur_nm": "아랍에미리트 디르함"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "AUD",
      |        "ttb": "861.77",
      |        "tts": "879.18",
      |        "deal_bas_r": "870.48",
      |        "bkpr": "870",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "870",
      |        "kftc_deal_bas_r": "870.48",
      |        "cur_nm": "호주 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "BHD",
      |        "ttb": "3,423.86",
      |        "tts": "3,493.03",
      |        "deal_bas_r": "3,458.45",
      |        "bkpr": "3,458",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "3,458",
      |        "kftc_deal_bas_r": "3,458.45",
      |        "cur_nm": "바레인 디나르"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "BND",
      |        "ttb": "960.14",
      |        "tts": "979.53",
      |        "deal_bas_r": "969.84",
      |        "bkpr": "969",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "969",
      |        "kftc_deal_bas_r": "969.84",
      |        "cur_nm": "브루나이 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "CAD",
      |        "ttb": "972.18",
      |        "tts": "991.82",
      |        "deal_bas_r": "982",
      |        "bkpr": "982",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "982",
      |        "kftc_deal_bas_r": "982",
      |        "cur_nm": "캐나다 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "CHF",
      |        "ttb": "1,457.82",
      |        "tts": "1,487.27",
      |        "deal_bas_r": "1,472.55",
      |        "bkpr": "1,472",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "1,472",
      |        "kftc_deal_bas_r": "1,472.55",
      |        "cur_nm": "스위스 프랑"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "CNH",
      |        "ttb": "178.35",
      |        "tts": "181.96",
      |        "deal_bas_r": "180.16",
      |        "bkpr": "180",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "180",
      |        "kftc_deal_bas_r": "180.16",
      |        "cur_nm": "위안화"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "DKK",
      |        "ttb": "190.56",
      |        "tts": "194.41",
      |        "deal_bas_r": "192.49",
      |        "bkpr": "192",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "192",
      |        "kftc_deal_bas_r": "192.49",
      |        "cur_nm": "덴마아크 크로네"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "EUR",
      |        "ttb": "1,420.09",
      |        "tts": "1,448.78",
      |        "deal_bas_r": "1,434.44",
      |        "bkpr": "1,434",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "1,434",
      |        "kftc_deal_bas_r": "1,434.44",
      |        "cur_nm": "유로"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "GBP",
      |        "ttb": "1,660.43",
      |        "tts": "1,693.98",
      |        "deal_bas_r": "1,677.21",
      |        "bkpr": "1,677",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "1,677",
      |        "kftc_deal_bas_r": "1,677.21",
      |        "cur_nm": "영국 파운드"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "HKD",
      |        "ttb": "164.89",
      |        "tts": "168.22",
      |        "deal_bas_r": "166.56",
      |        "bkpr": "166",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "166",
      |        "kftc_deal_bas_r": "166.56",
      |        "cur_nm": "홍콩 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "IDR(100)",
      |        "ttb": "8.49",
      |        "tts": "8.66",
      |        "deal_bas_r": "8.58",
      |        "bkpr": "8",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "8",
      |        "kftc_deal_bas_r": "8.58",
      |        "cur_nm": "인도네시아 루피아"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "JPY(100)",
      |        "ttb": "913.59",
      |        "tts": "932.04",
      |        "deal_bas_r": "922.82",
      |        "bkpr": "922",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "922",
      |        "kftc_deal_bas_r": "922.82",
      |        "cur_nm": "일본 옌"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "KRW",
      |        "ttb": "0",
      |        "tts": "0",
      |        "deal_bas_r": "1",
      |        "bkpr": "1",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "1",
      |        "kftc_deal_bas_r": "1",
      |        "cur_nm": "한국 원"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "KWD",
      |        "ttb": "4,205.52",
      |        "tts": "4,290.49",
      |        "deal_bas_r": "4,248.01",
      |        "bkpr": "4,248",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "4,248",
      |        "kftc_deal_bas_r": "4,248.01",
      |        "cur_nm": "쿠웨이트 디나르"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "MYR",
      |        "ttb": "276.39",
      |        "tts": "281.98",
      |        "deal_bas_r": "279.19",
      |        "bkpr": "279",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "279",
      |        "kftc_deal_bas_r": "279.19",
      |        "cur_nm": "말레이지아 링기트"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "NOK",
      |        "ttb": "123.17",
      |        "tts": "125.66",
      |        "deal_bas_r": "124.42",
      |        "bkpr": "124",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "124",
      |        "kftc_deal_bas_r": "124.42",
      |        "cur_nm": "노르웨이 크로네"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "NZD",
      |        "ttb": "801.56",
      |        "tts": "817.75",
      |        "deal_bas_r": "809.66",
      |        "bkpr": "809",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "809",
      |        "kftc_deal_bas_r": "809.66",
      |        "cur_nm": "뉴질랜드 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "SAR",
      |        "ttb": "344.08",
      |        "tts": "351.03",
      |        "deal_bas_r": "347.56",
      |        "bkpr": "347",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "347",
      |        "kftc_deal_bas_r": "347.56",
      |        "cur_nm": "사우디 리얄"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "SEK",
      |        "ttb": "119.99",
      |        "tts": "122.42",
      |        "deal_bas_r": "121.21",
      |        "bkpr": "121",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "121",
      |        "kftc_deal_bas_r": "121.21",
      |        "cur_nm": "스웨덴 크로나"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "SGD",
      |        "ttb": "960.14",
      |        "tts": "979.53",
      |        "deal_bas_r": "969.84",
      |        "bkpr": "969",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "969",
      |        "kftc_deal_bas_r": "969.84",
      |        "cur_nm": "싱가포르 달러"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "THB",
      |        "ttb": "36.79",
      |        "tts": "37.54",
      |        "deal_bas_r": "37.17",
      |        "bkpr": "37",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "37",
      |        "kftc_deal_bas_r": "37.17",
      |        "cur_nm": "태국 바트"
      |    },
      |    {
      |        "result": 1,
      |        "cur_unit": "USD",
      |        "ttb": "1,290.76",
      |        "tts": "1,316.83",
      |        "deal_bas_r": "1,303.8",
      |        "bkpr": "1,303",
      |        "yy_efee_r": "0",
      |        "ten_dd_efee_r": "0",
      |        "kftc_bkpr": "1,303",
      |        "kftc_deal_bas_r": "1,303.8",
      |        "cur_nm": "미국 달러"
      |    }
      |]
      |""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Koreaexim API Test") (
      test("decoding") {
        val value = res_sample.fromJson[List[Koreaexim.KoreaeximResponse]]
//        val value = Koreaexim.KoreaeximResponseList.koreaeximResponseListDecoder.decodeJson(res_sample)


        val dollorExchange = value.map(_.find(_.cur_unit == "USD"))
        val dollorExchangeRate = dollorExchange match {
          case Left(_) => "fail"
          case Right(value) => value.map(_.deal_bas_r).getOrElse("")
        }

        assertTrue(dollorExchangeRate == "1,303.8")
      },
      test("foramt") {
        val formatted = "1,303.8".replaceAll(",", "").toDouble
        assertTrue(formatted == 1303.8)
      },
      test("get date") {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val res = LocalDate.of(2023, 7, 15).format(formatter)
        assertTrue(res == "20230715")
      },
      test("get date") {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val res = LocalDate.of(2023, 7, 15).minusDays(1).format(formatter)
        assertTrue(res == "20230714")
      }
    )
}
