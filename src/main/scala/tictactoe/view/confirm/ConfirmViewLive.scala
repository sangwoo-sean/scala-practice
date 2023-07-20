package tictactoe.view.confirm

import tictactoe.domain._
import zio._

final case class ConfirmViewLive() extends ConfirmView {
  def header(action: ConfirmAction): UIO[String] = ZIO.succeed(action).map {
    case ConfirmAction.NewGame =>
      """[New game]
        |
        |This will discard current game progress.""".stripMargin
    case ConfirmAction.Quit =>
      """[Quit]
        |
        |This will discard current game progress.""".stripMargin
  }

  val content: UIO[String] =
    ZIO.succeed(
      """Are you sure?
        |<yes> / <no>""".stripMargin
    )

  def footer(message: ConfirmFooterMessage): UIO[String] =
    ZIO.succeed(message) map {
      case ConfirmFooterMessage.Empty          => ""
      case ConfirmFooterMessage.InvalidCommand => "Invalid command. Try again."
    }
}
object ConfirmViewLive {
  val layer: ULayer[ConfirmView] = ZLayer.succeed(ConfirmViewLive())
}