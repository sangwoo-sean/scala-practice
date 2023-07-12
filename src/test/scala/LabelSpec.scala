import zio.Scope
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object LabelSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("test")(
      test("a") {
        println("this is a")
        assertTrue(true)
      },
      test("a b") {
        println("this is a b")
        assertTrue(true)
      },
      test("b c") {
        println("this is b c")
        assertTrue(true)
      }
    ) +
      suite("test2")(
        test("a") {
          println("this is a")
          assertTrue(true)
        },
        test("a b") {
          println("this is a b")
          assertTrue(true)
        },
        test("b c") {
          println("this is b c")
          assertTrue(true)
        }
      )

}
