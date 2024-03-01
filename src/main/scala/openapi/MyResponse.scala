package openapi

import zio.schema.{DeriveSchema, Schema}

final case class MyResponse(
    myInt: Int,
    myString: String,
)

object MyResponse {
  implicit val myResSchema: Schema[MyResponse] = DeriveSchema.gen

  val example1 = MyResponse(1, "1")
  val example2 = MyResponse(2, "2")
}
