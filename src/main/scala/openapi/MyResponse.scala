package openapi

import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.schema.{DeriveSchema, Schema}

final case class MyResponse(
    myInt: Int,
    myString: String,
    myEnum: MyEnum
)

object MyResponse {
  implicit val myResSchema: Schema[MyResponse]   = DeriveSchema.gen
  implicit val myResCodec: JsonCodec[MyResponse] = DeriveJsonCodec.gen

  val example1 = MyResponse(1, "1", MyEnum.A)
  val example2 = MyResponse(2, "2", MyEnum.B)
}

sealed trait MyEnum

case object MyEnum {
  implicit val schema: Schema[MyEnum]         = DeriveSchema.gen
  implicit val myEnumCodec: JsonCodec[MyEnum] = DeriveJsonCodec.gen
  case object A extends MyEnum
  case object B extends MyEnum
}
