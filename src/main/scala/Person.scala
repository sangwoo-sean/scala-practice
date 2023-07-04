import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Person(id: Long, name: String, age: Int)

object Person {
  implicit val encoder: JsonEncoder[Person] =
    DeriveJsonEncoder.gen[Person]
  implicit val decoder: JsonDecoder[Person] =
    DeriveJsonDecoder.gen[Person]
}

case class PersonRequest(name: String, age: Int)

object PersonRequest {
  implicit val encoder: JsonEncoder[PersonRequest] =
    DeriveJsonEncoder.gen[PersonRequest]
  implicit val decoder: JsonDecoder[PersonRequest] =
    DeriveJsonDecoder.gen[PersonRequest]
}
