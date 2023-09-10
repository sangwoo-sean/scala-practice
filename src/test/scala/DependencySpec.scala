import zio._
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object DependencySpec extends ZIOSpecDefault {
  type TestEnv = A with B

  val layer: ZLayer[Any, Nothing, TestEnv] =
    ZLayer.succeed(new A) ++ ZLayer.succeed(new B)
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
    ).provide(layer)
}

final class A {
  def foo: UIO[String] = ZIO.succeed("Hello!")
}

final class B {
  def bar: UIO[Int] = ZIO.succeed(42)
}