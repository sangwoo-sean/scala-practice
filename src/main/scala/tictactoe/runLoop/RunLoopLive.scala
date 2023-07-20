package tictactoe.runLoop

import tictactoe.controller.Controller
import tictactoe.domain.State
import tictactoe.terminal.Terminal
import zio._


final case class RunLoopLive(controller: Controller, terminal: Terminal) extends RunLoop {
  override def step(state: State): UIO[Option[State]] =
    for {
      _         <- controller.render(state).flatMap(terminal.display)
      input     <- if (state == State.Shutdown) ZIO.succeed("") else terminal.getUserInput
      nextState <- controller.process(input, state)
    } yield nextState
}
object RunLoopLive {
  val layer: URLayer[Controller with Terminal, RunLoop] = ZLayer.fromFunction(RunLoopLive(_, _))
}