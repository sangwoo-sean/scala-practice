import zio._
import zio.test._
import zio.json._
import zio.json.ast.Json

object ZIOJsonASTSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ZIOJsonAST")(
      suite("parse json")(
        test("normal") {
          val str = """{"a":"hello","b": {"c": 1000}}"""

          val result = for {
            content <- str.fromJson[Json].toOption
            aValue  <- content.get(ast.JsonCursor.field("a")).toOption
            aString <- aValue.asString
          } yield aString

          result match {
            case Some(r) => assertTrue(r == "hello")
            case None    => assertNever("failed to parse")
          }
        },
        test("with raw new line") {
          val str =
            """{
              |"a":"hello","b": {"c": 1000}
              |}
              |""".stripMargin

          val result = for {
            content <- str.fromJson[Json].toOption
            aValue  <- content.get(ast.JsonCursor.field("a")).toOption
            aString <- aValue.asString
          } yield aString

          result match {
            case Some(r) => assertTrue(r == "hello")
            case None    => assertNever("failed to parse")
          }
        },
      ),
      test("parse inner object") {
        val str = """{"a":"hello","b": {"c": 1000}}"""

        val result = for {
          content <- str.fromJson[Json].toOption
          bField  <- content.get(ast.JsonCursor.field("b")).toOption
          cField  <- bField.asObject.flatMap(_.get("c"))
          cNumber <- cField.asNumber
        } yield BigDecimal(cNumber.value)

        result match {
          case Some(r) => assertTrue(r == BigDecimal(1000))
          case None    => assertNever("failed to parse")
        }
      }
    )

}
