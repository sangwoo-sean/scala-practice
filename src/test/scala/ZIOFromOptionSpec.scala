import zio.test._
import zio._
import zio.test.Assertion.{anything, fails, failsWithA, isSubtype}

object ZIOFromOptionSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ZIO.fromOption")(
      test("success") {
        for {
          result <- ZIO.fromOption[Int](Some(1))
        } yield assertTrue(result == 1)
      },
      test("fail") {
        for {
          result <- ZIO.fromOption[Int](None).exit
        } yield assert(result)(fails(anything) && fails(isSubtype[Option[Nothing]](anything)))
      },
    )

}
