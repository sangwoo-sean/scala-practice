package openapi

import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.schema.{DeriveSchema, Schema}

final case class MyRequest(
    myInt: Int,
    myString: String,
)

object MyRequest {
  implicit val myReqSchema: Schema[MyRequest]   = DeriveSchema.gen
  implicit val myReqCodec: JsonCodec[MyRequest] = DeriveJsonCodec.gen[MyRequest]

  val example1 = MyRequest(1, "1")
  val example2 = MyRequest(2, "2")
}
