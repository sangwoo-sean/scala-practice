import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.ztapir.{oneOf, oneOfVariant, statusCode}
import sttp.tapir.{endpoint, EndpointOutput}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder}

case class KoreaeximResponse(
    RESULT: Int,          //	Integer	조회 결과	1 : 성공, 2 : DATA코드 오류, 3 : 인증코드 오류, 4 : 일일제한횟수 마감
    CUR_UNIT: String,        //	String	통화코드
    CUR_NM: String,          //	String	국가/통화명
    TTB: String,             //	String	전신환(송금) 받으실때
    TTS: String,             //	String	전신환(송금) 보내실때
    DEAL_BAS_R: String,      //	String	매매 기준율
    BKPR: String,            //	String	장부가격
    YY_EFEE_R: String,       //	String	년환가료율
    TEN_DD_EFEE_R: String,   //	String	10일환가료율
    KFTC_DEAL_BAS_R: String, //	String	서울외국환중개 매매기준율
    KFTC_BKPR: String,       //	String	서울외국환중개 장부가격
)
object KoreaeximResponse {
  implicit val koreaeximResponseEncoder: zio.json.JsonEncoder[KoreaeximResponse] = DeriveJsonEncoder.gen[KoreaeximResponse]
  implicit val koreaeximResponseDecoder: zio.json.JsonDecoder[KoreaeximResponse] = DeriveJsonDecoder.gen[KoreaeximResponse]
}

class KoreaeximApiGateway {

  val getExchangeRateEndpoint =
    endpoint
      .errorOut(defaultErrorOutputs)
      .get
      .in("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON")
      .out(jsonBody[KoreaeximResponse])

  val defaultErrorOutputs: EndpointOutput.OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[BadRequest])),
    oneOfVariant(statusCode(StatusCode.Forbidden).and(jsonBody[Forbidden])),
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
    oneOfVariant(statusCode(StatusCode.Conflict).and(jsonBody[Conflict])),
    oneOfVariant(statusCode(StatusCode.Unauthorized).and(jsonBody[Unauthorized])),
    oneOfVariant(statusCode(StatusCode.UnprocessableEntity).and(jsonBody[ValidationFailed])),
    oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[InternalServerError]))
  )

}

sealed trait ErrorInfo
case class BadRequest(error: String = "Bad request.")                    extends ErrorInfo
case class Unauthorized(error: String = "Unauthorized.")                 extends ErrorInfo
case class Forbidden(error: String = "Forbidden.")                       extends ErrorInfo
case class NotFound(error: String = "Not found.")                        extends ErrorInfo
case class Conflict(error: String = "Conflict.")                         extends ErrorInfo
case class ValidationFailed(errors: Map[String, List[String]])           extends ErrorInfo
case class InternalServerError(error: String = "Internal server error.") extends ErrorInfo

object ErrorInfo {
  implicit val badRequestEncoder: zio.json.JsonEncoder[BadRequest]             = DeriveJsonEncoder.gen[BadRequest]
  implicit val badRequestDecoder: zio.json.JsonDecoder[BadRequest]             = DeriveJsonDecoder.gen[BadRequest]
  implicit val forbiddenEncoder: zio.json.JsonEncoder[Forbidden]               = DeriveJsonEncoder.gen[Forbidden]
  implicit val forbiddenDecoder: zio.json.JsonDecoder[Forbidden]               = DeriveJsonDecoder.gen[Forbidden]
  implicit val notFoundEncoder: zio.json.JsonEncoder[NotFound]                 = DeriveJsonEncoder.gen[NotFound]
  implicit val notFoundDecoder: zio.json.JsonDecoder[NotFound]                 = DeriveJsonDecoder.gen[NotFound]
  implicit val conflictEncoder: zio.json.JsonEncoder[Conflict]                 = DeriveJsonEncoder.gen[Conflict]
  implicit val conflictDecoder: zio.json.JsonDecoder[Conflict]                 = DeriveJsonDecoder.gen[Conflict]
  implicit val unauthorizedEncoder: zio.json.JsonEncoder[Unauthorized]         = DeriveJsonEncoder.gen[Unauthorized]
  implicit val unauthorizedDecoder: zio.json.JsonDecoder[Unauthorized]         = DeriveJsonDecoder.gen[Unauthorized]
  implicit val validationFailedEncoder: zio.json.JsonEncoder[ValidationFailed] = DeriveJsonEncoder.gen[ValidationFailed]
  implicit val validationFailedDecoder: zio.json.JsonDecoder[ValidationFailed] = DeriveJsonDecoder.gen[ValidationFailed]
  implicit val internalServerErrorEncoder: zio.json.JsonEncoder[InternalServerError] =
    DeriveJsonEncoder.gen[InternalServerError]
  implicit val internalServerErrorDecoder: zio.json.JsonDecoder[InternalServerError] =
    DeriveJsonDecoder.gen[InternalServerError]
}
