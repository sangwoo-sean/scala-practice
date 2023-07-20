package tictactoe.view.confirm

import tictactoe.domain._
import zio._

trait ConfirmView {
  def header(action: ConfirmAction): UIO[String]
  def content: UIO[String]
  def footer(message: ConfirmFooterMessage): UIO[String]
}

object ConfirmView {
  def header(action: ConfirmAction) =
    ZIO.serviceWithZIO[ConfirmView](_.header(action))
  def content =
    ZIO.serviceWithZIO[ConfirmView](_.content)
  def footer(message: ConfirmFooterMessage) =
    ZIO.serviceWithZIO[ConfirmView](_.footer(message))
}