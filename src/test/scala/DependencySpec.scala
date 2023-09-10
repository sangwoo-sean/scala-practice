import zio._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object DependencySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("dependency") (
      test("service") {
        for {
          a <- ZIO.service[A]
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
    ).provide(A.layer ++ B.layer)
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