import zio.Scope
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object ProductSpec extends ZIOSpecDefault {

  case class Member(id: Int, lastName: String, firstName: String)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("product")(
      test("methods") {
        val sangwoo = Member(1, "Sangwoo", "Park")

        assertTrue(
          sangwoo.productArity == 3 && // argument 개수
            sangwoo.productElement(0) == 1 &&
            sangwoo.productElement(1) == "Sangwoo" &&
            sangwoo.productElement(2) == "Park" &&
            sangwoo.productPrefix == "Member" && // class 이름
            sangwoo.productIterator.mkString(",") == "1,Sangwoo,Park" &&
            sangwoo.productElementNames.mkString(",") == "id,lastName,firstName"
        )
      }
    )

}
