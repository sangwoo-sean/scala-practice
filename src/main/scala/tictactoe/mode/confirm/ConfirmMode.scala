package tictactoe.mode.confirm

import tictactoe.domain.State
import zio.{UIO, ZIO}

trait ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State]
  def render(state: State.Confirm): UIO[String]
}

object ConfirmMode {
  def process(input: String, state: State.Confirm) =
    ZIO.serviceWithZIO[ConfirmMode](_.process(input, state))
  def render(state: State.Confirm) =
    ZIO.serviceWithZIO[ConfirmMode](_.render(state))
}