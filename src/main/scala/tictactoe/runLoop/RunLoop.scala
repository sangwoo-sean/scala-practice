package tictactoe.runLoop

import tictactoe.domain.State
import zio.{UIO, ZIO}

trait RunLoop {
  def step(state: State): UIO[Option[State]]
}

object RunLoop {
  def step(state: State) =
    ZIO.serviceWithZIO[RunLoop](_.step(state))
}