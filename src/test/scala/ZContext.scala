import zio._

object ZContext extends ZIOAppDefault {
  val manualDependency =
    for {
      _ <- ZIO.unit
      terminal = Terminal.TerminalLive() // 직접 만들어서 사용
      _ <- terminal.print("name? ")
      name <- terminal.readLine
      _ <- terminal.print(s"hello $name")
    } yield ()

  val withZLayer =
    for {
      terminal <- ZIO.service[Terminal] // R 로 provide 된 Terminal 을 사용한다.
      _ <- terminal.print("name? ")
      name <- terminal.readLine
      _ <- terminal.print(s"hello $name")
    } yield()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
//    manualDependency
//    withZLayer.provideLayer(Terminal.live)
    withZLayer.provideLayer(Terminal.liveX2)
  }
}

trait Terminal {
  def print(line: Any): Task[Unit]
  def readLine: Task[String]
}

object Terminal {

  case class TerminalLive() extends Terminal {
    override def print(line: Any): Task[Unit] =
      ZIO.attemptBlocking(scala.Predef.print(line))

    override def readLine: Task[String] =
      ZIO.attemptBlocking(scala.io.StdIn.readLine())
  }

  case class TerminalLiveX2() extends Terminal {
    override def print(line: Any): Task[Unit] =
      ZIO.attemptBlocking(scala.Predef.print(line)).repeatN(1)

    override def readLine: Task[String] =
      ZIO.attemptBlocking(scala.io.StdIn.readLine())
  }

  val live: ULayer[TerminalLive] = ZLayer.succeed(TerminalLive()) //Terminal 을 R 로 제공해준다.
  val liveX2: ULayer[TerminalLiveX2] = ZLayer.succeed(TerminalLiveX2()) //TerminalX2 를 R 로 제공해준다.
}