import zio.{Chunk, Scope}
import zio.json._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

@jsonDiscriminator("nation")
@jsonDerive
sealed trait Nation
object Nation {
  final object Korea extends Nation
  final object US extends Nation
  final object UK extends Nation
}

object ADTSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ADT with ZIO JSON") {
      test("discriminator and nations") {
        val korea: Nation = Nation.Korea
        val us: Nation = Nation.US
        val uk: Nation = Nation.UK

        val chunk = Chunk(korea, us, uk)

        val jsonString = chunk.toJson
        val expected = "[{\"nation\":\"Korea\"},{\"nation\":\"US\"},{\"nation\":\"UK\"}]"
        assertTrue(jsonString == expected)
      }
    }
}
