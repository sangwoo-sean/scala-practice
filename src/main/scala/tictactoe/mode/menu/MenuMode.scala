package tictactoe.mode.menu

import tictactoe.domain.State
import zio._

trait MenuMode {
  def process(input: String, state: State.Menu): UIO[State]
  def render(state: State.Menu): UIO[String]
}

object MenuMode {
  def process(input: String, state: State.Menu) =
    ZIO.serviceWithZIO[MenuMode](_.process(input, state))
  def render(state: State.Menu) =
    ZIO.serviceWithZIO[MenuMode](_.render(state))
}