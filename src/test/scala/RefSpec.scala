import zio.Scope
import zio.test._
import zio._

object RefSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("ref")(
      test("make and get") {
        for {
          init <- Ref.make(0)
          res  <- init.get
        } yield assertTrue(res == 0)
      },
      test("update") {
        for {
          init <- Ref.make(0)
          _    <- init.update(_ + 1)
          res  <- init.get
        } yield assertTrue(res == 1)
      },
      test("set") {
        for {
          init <- Ref.make(0)
          _    <- init.set(2)
          res  <- init.get
        } yield assertTrue(res == 2)
      },
      test("get and modify") {
        def func(v: Int): Int = v + 1

        for {
          init <- Ref.make(0)
          res1 <- init.modify { v =>
            val calc = func(v)
            (v, calc)
          }
          res2 <- init.get
        } yield assertTrue(res1 == 0 && res2 == 1)
      },
      test("modify and get") {
        def func(v: Int): Int = v + 1

        for {
          init <- Ref.make(0)
          res1 <- init.modify { v =>
            val calc = func(v)
            (calc, calc)
          }
          res2 <- init.get
        } yield assertTrue(res1 == 1 && res2 == 1)
      },
    )

}
