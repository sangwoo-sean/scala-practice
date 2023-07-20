package tictactoe.view.menu

import tictactoe.domain.MenuFooterMessage
import zio._

trait MenuView {
  def header: UIO[String]
  def content(isSuspended: Boolean): UIO[String]
  def footer(message: MenuFooterMessage): UIO[String]
}

object MenuView {
  def header =
    ZIO.serviceWithZIO[MenuView](_.header)
  def content(isSuspended: Boolean) =
    ZIO.serviceWithZIO[MenuView](_.content(isSuspended))
  def footer(message: MenuFooterMessage) =
    ZIO.serviceWithZIO[MenuView](_.footer(message))
}