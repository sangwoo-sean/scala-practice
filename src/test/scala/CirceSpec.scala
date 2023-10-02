import io.circe
import io.circe.parser
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.generic.{JsonCodec, semiauto}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

//@JsonCodec // you don't even need this because of Automatic Derivation
case class OurJson(
    textField: String,
    numericField: Int,
    booleanField: Boolean,
    nestedObject: Nested
)

//object OurJson { // replaced by @JsonCodec
//  implicit val jsonCodec: Codec[OurJson] = semiauto.deriveCodec[OurJson]
//}

//@JsonCodec // you don't even need this because of Automatic Derivation
case class Nested(arrayField: Vector[Int])

//object Nested { // replaced by @JsonCodec
//  implicit val nestedCodec: Codec[Nested] = semiauto.deriveCodec[Nested]
//}

object CirceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Circe")(
      test("basic") {
        val jsonString = """{"value": 1}"""

        val json = parser.parse(jsonString).getOrElse(throw new RuntimeException("failed to parse"))

        val result = json.as[Map[String, Int]].getOrElse(throw new RuntimeException("failed to decode"))

        assertTrue(result("value") == 1 && result.get("nope").isEmpty)
      },
      test("decode with Decoder") {
        val jsonString =
          """
            |{
            | "textField": "textContent",
            | "numericField": 123,
            | "booleanField": true,
            | "nestedObject": {
            | "arrayField": [1, 2, 3]
            | }
            |}
            |""".stripMargin

        val parsedJson =
          circe.parser.decode[OurJson](jsonString).getOrElse(throw new RuntimeException("failed to parse"))

        assertTrue(
          parsedJson.textField == "textContent" &&
            parsedJson.numericField == 123 &&
            parsedJson.booleanField &&
            parsedJson.nestedObject.arrayField == Vector(1, 2, 3)
        )
      },
      test("decode json from parsed Json") {
        val jsonString =
          """
            |{
            | "textField": "textContent",
            | "numericField": 123,
            | "booleanField": true,
            | "nestedObject": {
            | "arrayField": [1, 2, 3]
            | }
            |}
            |""".stripMargin

        val parsedJson  = parser.parse(jsonString).getOrElse(throw new RuntimeException("failed to parse"))
        val decodedJson = parsedJson.as[OurJson].getOrElse(throw new RuntimeException("failed to decode"))

        assertTrue(
          decodedJson.textField == "textContent" &&
            decodedJson.numericField == 123 &&
            decodedJson.booleanField &&
            decodedJson.nestedObject.arrayField == Vector(1, 2, 3)
        )
      }
    )

}
