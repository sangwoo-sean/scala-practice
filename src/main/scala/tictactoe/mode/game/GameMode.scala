package tictactoe.mode.game

import tictactoe.domain.State
import zio.{UIO, ZIO}

trait GameMode {
  def process(input: String, state: State.Game): UIO[State]
  def render(state: State.Game): UIO[String]
}

object GameMode {
  def process(input: String, state: State.Game) =
    ZIO.serviceWithZIO[GameMode](_.process(input, state))
  def render(state: State.Game) =
    ZIO.serviceWithZIO[GameMode](_.render(state))
}
