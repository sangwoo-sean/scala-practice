import zio._
import zio.macros.ServiceReloader

import java.io.IOException
import java.util.UUID

object ServiceReloaderApp extends ZIOAppDefault {

  trait Counter {
    def increment: UIO[Unit]

    def get: UIO[Int]
  }

  object Counter {
    val increment: ZIO[Counter, Nothing, Unit] =
      ZIO.serviceWithZIO[Counter](_.increment)

    val get: ZIO[Counter, Nothing, RuntimeFlags] =
      ZIO.serviceWithZIO[Counter](_.get)

    val live: ZLayer[Any, IOException, Counter] = ZLayer.scoped {
      for {
        _   <- Console.printLine("loading...")
        id  <- Ref.make(UUID.randomUUID())
        ref <- Ref.make(0)
        service = CounterLive(id, ref)
        _ <- service.acquire
        _ <- ZIO.addFinalizer(service.release)
        _ <- Console.printLine("loaded")
      } yield service
    }

    val reloadable: ZLayer[Any, IOException, Reloadable[Counter]] =
      live.reloadableAuto(Schedule.fixed(3.seconds))

    val reloadableMacro: ZLayer[ServiceReloader, ServiceReloader.Error, Counter] = {
      ZLayer.fromZIO(ServiceReloader.register(live))
    }
  }

  final case class CounterLive(id: Ref[UUID], ref: Ref[Int]) extends Counter {

    def acquire: UIO[Unit] =
      Random.nextUUID
        .flatMap(n => id.set(n) *> ZIO.debug(s"Acquired counter $n"))

    def increment: UIO[Unit] =
      ref.update(_ + 1)

    def get: UIO[Int] =
      ref.get

    def release: UIO[Unit] =
      id.get.flatMap(id => ZIO.debug(s"Released counter $id"))
  }

  def app: ZIO[Counter with ServiceReloader, ServiceReloader.Error, Unit] =
    for {
//      _ <- ServiceReloader.reload[Counter].schedule(Schedule.fixed(1.second)).fork
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")

      _ <- ZIO.sleep(3.seconds)

      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.increment
      _ <- Counter.get.debug("Counter value")
    } yield ()

  override def run =
    (app <&> ServiceReloader.reload[Counter].schedule(Schedule.fixed(1.second))).provide(Counter.reloadableMacro, ServiceReloader.live)
}
