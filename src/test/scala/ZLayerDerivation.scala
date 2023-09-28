import zio._

object ZLayerDerivation extends ZIOAppDefault {

  val myProgramLogic =
    for {
      _ <- ServiceA.doSomething
      _ <- ServiceA.doSomething
    } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    myProgramLogic.provide(
      ServiceA.auto,
      ServiceB.live,
      ServiceC.live,
    )
}

trait ServiceA {
  def doSomething: ZIO[Any, Nothing, Unit]
}

object ServiceA {

  val doSomething: ZIO[ServiceA, Nothing, Unit] =
    ZIO.serviceWithZIO(_.doSomething)

  private final case class ServiceALive(serviceB: ServiceB, serviceC: ServiceC) extends ServiceA {

    override def doSomething: ZIO[Any, Nothing, Unit] =
      for {
        _ <- serviceB.doSomethingElse
        _ <- serviceC.doSomethingElseAgain
        _ <- ZIO.debug("Hello!")
      } yield ()
  }

  val manual: ZLayer[ServiceB with ServiceC, Nothing, ServiceA] =
    ZLayer {
      for {
        _ <- ZIO.debug("init!!!")
        serviceB <- ZIO.service[ServiceB]
        serviceC <- ZIO.service[ServiceC]
        _ <- ZIO.debug("finish!!!")
      } yield ServiceALive(serviceB, serviceC)
    }

  val auto: ZLayer[ServiceB with ServiceC, Nothing, ServiceA] =
    ZLayer.derive[ServiceALive]

}

trait ServiceB {
  def doSomethingElse: ZIO[Any, Nothing, Unit]
}

object ServiceB {

  final case class ServiceBLive() extends ServiceB {
    override def doSomethingElse: ZIO[Any, Nothing, Unit] = someHelperMethod *> ZIO.unit
    def someHelperMethod: UIO[Unit] = ZIO.debug("helper")
  }
  val live: ULayer[ServiceBLive] = ZLayer.succeed(ServiceBLive())
}

trait ServiceC {
  def doSomethingElseAgain: ZIO[Any, Nothing, Unit]
}

object ServiceC {

  final case class ServiceCLive() extends ServiceC {
    override def doSomethingElseAgain: ZIO[Any, Nothing, Unit] = ZIO.unit
  }

  val live: ULayer[ServiceCLive] = ZLayer.succeed(ServiceCLive())

}
