import zio._

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

  def app: ZIO[Reloadable[Counter], Any, Unit] =
    for {
      reloadable <- ZIO.service[Reloadable[Counter]]
      counter <- reloadable.get
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.get.debug("Counter value")

      _ <- ZIO.sleep(5.seconds)

      counter <- reloadable.get
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.increment
      _ <- counter.get.debug("Counter value")
    } yield ()

  override def run =
    app.provide(Counter.reloadable)
}
