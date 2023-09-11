import zio._
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object DependencySpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("dependency")(
      test("service") {
        for {
          a   <- ZIO.service[A]
          res <- a.foo
        } yield assertTrue(res == "Hello!")
      },
      test("serviceWithZIO") {
        for {
          foo <- ZIO.serviceWithZIO[A](_.foo)
          bar <- ZIO.serviceWithZIO[B](_.bar)
        } yield assertTrue(foo == "Hello!" && bar == 42)
      },
      test("Sequential Composition") {
        for {
          foo <- ZIO.serviceWithZIO[A](_.foo)
          bar <- ZIO.serviceWithZIO[B](_.bar)
        } yield assertTrue(foo == "Hello!" && bar == 42)
      },
      test("Vertical Composition") {
        for {
          res <- ZIO.serviceWithZIO[C](_.baz)
        } yield assertTrue(res == "Hello!42")
      }
    ).provide(
      A.layer,
      B.layer,
      (A.layer ++ B.layer) >>>
        C.layer,
//      C.layer2
    )

}

final class A {
  def foo: UIO[String] = ZIO.succeed("Hello!")
}

object A {
  val layer: ULayer[A] = ZLayer.succeed(new A)
}

final class B {
  def bar: UIO[Int] = ZIO.succeed(42)
}

object B {
  val layer: ULayer[B] = ZLayer.succeed(new B)
}

final case class C(a: A, b: B) {

  def baz: ZIO[Any, Nothing, String] =
    for {
      f <- a.foo
      b <- b.bar
    } yield f + b.toString

}

object C {
  def baz = ZIO.serviceWithZIO[C](_.baz)

  val layer: ZLayer[B with A, Nothing, C] = ZLayer {
    for {
      a <- ZIO.service[A]
      b <- ZIO.service[B]
    } yield C(a, b)
  }

  val layer2: ULayer[C] = ZLayer.succeed(C(new A, new B))
}
