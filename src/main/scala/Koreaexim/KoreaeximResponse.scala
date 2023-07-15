package Koreaexim

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

case class KoreaeximResponse(
    result: Int,             //	Integer	조회 결과	1 : 성공, 2 : DATA코드 오류, 3 : 인증코드 오류, 4 : 일일제한횟수 마감
    cur_unit: String,        //	String	통화코드
    cur_nm: String,          //	String	국가/통화명
    ttb: String,             //	String	전신환(송금) 받으실때
    tts: String,             //	String	전신환(송금) 보내실때
    deal_bas_r: String,      //	String	매매 기준율
    bkpr: String,            //	String	장부가격
    yy_efee_r: String,       //	String	년환가료율
    ten_dd_efee_r: String,   //	String	10일환가료율
    kftc_deal_bas_r: String, //	String	서울외국환중개 매매기준율
    kftc_bkpr: String,       //	String	서울외국환중개 장부가격
)

object KoreaeximResponse {
  implicit val koreaeximResponseEncoder: zio.json.JsonEncoder[KoreaeximResponse] =
    DeriveJsonEncoder.gen[KoreaeximResponse]
  implicit val koreaeximResponseDecoder: zio.json.JsonDecoder[KoreaeximResponse] =
    DeriveJsonDecoder.gen[KoreaeximResponse]
}

