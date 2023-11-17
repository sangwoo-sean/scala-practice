import zio._
import zio.test._

object CauseSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("Cause")(
      test("Fail") {
        for {
          res <- ZIO.fail(new Throwable("test")).catchSomeCause {
            case c @ Cause.Fail(value, trace) =>
              ZIO.succeed((value.getMessage, c.isFailure))
          }
          (value, isFailure) = res
        } yield assertTrue(value == "test" && isFailure)
      },
      test("Empty") {
        for {
          isEmpty <- ZIO.failCause(Cause.Empty).catchSomeCause { case c @ Cause.Empty => ZIO.succeed(c.isEmpty) }
        } yield assertTrue(isEmpty)
      },
      test("Die") {
        for {
          isDie <- ZIO
            .failCause(new Cause.Die(new Throwable("test"), StackTrace.none))
            .catchSomeCause {
              case c: Cause.Die =>
                ZIO.succeed(c.isDie)
            }
        } yield assertTrue(isDie)
      },
      suite("Interrupt")(
        test("Interrupt") {
          for {
            result <- ZIO
              .failCause(new Cause.Interrupt(FiberId.None, StackTrace.none))
              .catchSomeCause {
                case c: Cause.Interrupt =>
                  ZIO.succeed(c.isInterrupted -> c.isInterruptedOnly)
              }
            (isInterrupted, isInterruptedOnly) = result
          } yield assertTrue(isInterrupted && isInterruptedOnly)
        },
        test("Interrupted") {
          for {
            _ <- ZIO.never.fork.flatMap(f => f.interrupt *> f.join).cause.debug
          } yield assertTrue(true)
        }
      ),
      test("Stackless") {
        for {
          isDie <- ZIO
            .dieMessage("test")
            .catchSomeCause {
              case c @ Cause.Stackless(cause, stackless) =>
                ZIO.succeed(c.isDie)
            }
        } yield assertTrue(isDie)
      },
      test("Both") {
        for {
          f1 <- ZIO.fail("1").fork
          f2 <- ZIO.dieMessage("2").fork
          result <- (f1 <*> f2).join.catchAllCause {
            case Cause.Both(f1, f2) =>
              ZIO.succeed(f1.isFailure -> f2.isDie)
          }
          (isFailure, isDie) = result
        } yield assertTrue(isFailure && isDie)
      },
      test("Then") {
        for {
          result <- ZIO
            .fail("1")
            .ensuring(ZIO.die(throw new Exception("2")))
            .catchSomeCause {
              case c @ Cause.Then(left, right) =>
                ZIO.succeed(left.isFailure -> right.isDie)
            }
          (isFailure, isDie) = result
        } yield assertTrue(isFailure && isDie)
      }
    )

}
