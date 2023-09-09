import Animal.{Cat, Dog}
import zio.{Chunk, Scope}
import zio.json._
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

@jsonDiscriminator("nation")
@jsonDerive
sealed trait Nation

object Nation {
  @jsonHint("korea")
  final case object Korea extends Nation
  @jsonHint("us")
  final case object US    extends Nation
  @jsonHint("uk")
  final case object UK    extends Nation
}

@jsonDerive
sealed trait Animal

object Animal {
  @jsonDerive
  final case class Cat(name: String, doggy: Boolean) extends Animal
  @jsonDerive
  final case class Dog(name: String, size: Size) extends Animal

  sealed trait Size

  object Size {
    final case object Small  extends Size
    final case object Medium extends Size
    final case object Large  extends Size
    implicit val encoder: JsonEncoder[Size] = JsonEncoder[String].contramap(_.toString) // manual encoder
    implicit val decoder: JsonDecoder[Size] = DeriveJsonDecoder.gen[Size]
  }

}

object ADTSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ADT with ZIO JSON")(
      test("sealed trait & case object") {
        val korea: Nation = Nation.Korea
        val us: Nation    = Nation.US
        val uk: Nation    = Nation.UK

        val chunk = Chunk(korea, us, uk)

        val jsonString = chunk.toJson
        val expected   = """[{"nation":"korea"},{"nation":"us"},{"nation":"uk"}]"""
        assertTrue(jsonString == expected)
      },
      test("sealed trait & case class") {
        val cat1: Cat = Cat("navy", doggy = false)
        val cat2: Cat = Cat("nero", doggy = true)

        val chunk      = Chunk(cat1, cat2)
        val jsonString = chunk.toJson
        val expected   = """[{"name":"navy","doggy":false},{"name":"nero","doggy":true}]"""
        assertTrue(jsonString == expected)
      },
      test("sealed trait & case object & case class") {
        val dog1: Dog = Dog("choco", Animal.Size.Small)
        val dog2: Dog = Dog("dubu", Animal.Size.Large)

        val chunk      = Chunk(dog1, dog2)
        val jsonString = chunk.toJson
        val expected   = """[{"name":"choco","size":"Small"},{"name":"dubu","size":"Large"}]"""
        assertTrue(jsonString == expected)
      },
      test("sealed trait & case object & case class") {
        val cat1: Animal = Cat("navy", doggy = false)
        val cat2: Animal = Cat("nero", doggy = true)
        val dog1: Animal = Dog("choco", Animal.Size.Small)
        val dog2: Animal = Dog("dubu", Animal.Size.Large)

        val chunk      = Chunk(cat1, cat2, dog1, dog2)
        val jsonString = chunk.toJson
        val expected =
          """[{"Cat":{"name":"navy","doggy":false}},{"Cat":{"name":"nero","doggy":true}},{"Dog":{"name":"choco","size":"Small"}},{"Dog":{"name":"dubu","size":"Large"}}]""".stripMargin
        assertTrue(jsonString == expected)
      }
    )

}
