package openapi

import zio.schema.{DeriveSchema, Schema}

final case class MyResponse(
    myInt: Int,
    myString: String,
)

object MyResponse {
  implicit val myResSchema: Schema[MyResponse] = DeriveSchema.gen
}
