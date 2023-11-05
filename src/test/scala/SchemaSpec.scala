import zio._
import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.schema.codec.JsonCodec._
import zio.schema.{DeriveSchema, Schema}
import zio.test._
case class PersonDTO(firstName: String, lastName: String, age: Int)

object PersonDTO {
  // zio.schema 를 zio.json 에 등록하여 사용
  implicit val schema: Schema[PersonDTO]   = DeriveSchema.gen
  implicit val codec: JsonCodec[PersonDTO] = jsonCodec[PersonDTO](schema)

  // zio.json 으로 사용하던 방법
//  implicit val codec: JsonCodec[PersonDTO] = DeriveJsonCodec.gen[PersonDTO]
}

case class Person(name: String, age: Int)

object Person {
  // Person JSON -> Person
//  implicit val schema: Schema[Person] = DeriveSchema.gen
//  implicit val codec: JsonCodec[Person] = jsonCodec[Person](schema)

  // 위 두줄과 아래 한줄은 같은 역할을 함
  implicit val codec: JsonCodec[Person] = DeriveJsonCodec.gen[Person]

  val personDTOMapperSchema: Schema[Person] =
    PersonDTO.schema.transform(
      f = (dto: PersonDTO) => { // PersonDTO -> Person
        val name = s"${dto.firstName} ${dto.lastName}"
        Person(name, dto.age)
      },
      g = (person: Person) => { // Person -> PersonDTO
        val fullNameArray = person.name.split(" ")
        PersonDTO(
          firstName = fullNameArray.head,
          lastName = fullNameArray.last,
          age = person.age
        )
      }
    )

  // PersonDTO JSON -> Person
  val personDTOJsonMapperCodec: JsonCodec[Person] = jsonCodec[Person](personDTOMapperSchema)

  val personDTOJsonMapperCodec2: JsonCodec[Person] = DeriveJsonCodec
    .gen[PersonDTO]
    .transform(
      f = (dto: PersonDTO) => {
        val name = s"${dto.firstName} ${dto.lastName}"
        Person(name, dto.age)
      },
      g = (person: Person) => {
        val fullNameArray = person.name.split(" ")
        PersonDTO(
          firstName = fullNameArray.head,
          lastName = fullNameArray.last,
          age = person.age
        )
      }
    )

  def fromPersonDTO(p: PersonDTO): IO[String, Person] =
    ZIO.fromEither(
      PersonDTO.schema.migrate(personDTOMapperSchema).flatMap(_(p))
    )

}

object SchemaSpec extends ZIOSpecDefault {

  val personDTOJson: String =
    """
      |{
      |   "firstName": "John",
      |   "lastName": "Doe",
      |   "age": 19
      |}
      |""".stripMargin

  val personJson: String =
    """
      |{
      |   "name": "John Doe",
      |   "age": 19
      |}
      |""".stripMargin

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("schema")(
      test("PersonDTO JSON -> PersonDTO -> Person using ZIO JSON") {
        for {
          personDTO <- ZIO.fromEither(JsonCodec[PersonDTO].decodeJson(personDTOJson))

          person = Person(
            name = s"${personDTO.firstName} ${personDTO.lastName}",
            age = personDTO.age
          )
        } yield assertTrue(person.name == "John Doe" && person.age == 19)
      },
      test("Person JSON -> Person using ZIO JSON") {
        for {
          person <- ZIO.fromEither(
            JsonCodec[Person](Person.codec).decodeJson(personJson)
          )
        } yield assertTrue(person.name == "John Doe" && person.age == 19)
      },
      test("PersonDTO JSON -> Person using Schema") {
        for {
          person <- ZIO.fromEither(
            JsonCodec[Person](Person.personDTOJsonMapperCodec).decodeJson(personDTOJson)
          )
        } yield assertTrue(person.name == "John Doe" && person.age == 19)
      },
      test("PersonDTO JSON -> Person using ZIO JSON") {
        for {
          person <- ZIO.fromEither(
            JsonCodec[Person](Person.personDTOJsonMapperCodec2).decodeJson(personDTOJson)
          )
        } yield assertTrue(person.name == "John Doe" && person.age == 19)
      },
      test("PersonDTO -> Person using ZIO Schema migrate") {
        for {
          personDTO <- ZIO.succeed(PersonDTO("John", "Doe", 19))
          person <- Person.fromPersonDTO(personDTO)
        } yield assertTrue(person.name == "John Doe" && person.age == 19)
      }
    )

}
