package tictactoe.terminal

import zio._

trait Terminal {
  def getUserInput: UIO[String]
  def display(frame: String): UIO[Unit]
}

object Terminal {
  def getUserInput =
    ZIO.serviceWithZIO[Terminal](_.getUserInput)

  def display(frame: String) =
    ZIO.serviceWithZIO[Terminal](_.display(frame))
}
