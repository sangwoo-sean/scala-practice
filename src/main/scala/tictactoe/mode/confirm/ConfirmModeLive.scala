package tictactoe.mode.confirm

import tictactoe.domain.{ConfirmCommand, ConfirmFooterMessage, State}
import tictactoe.parser.confirm.game.ConfirmCommandParser
import tictactoe.view.confirm.ConfirmView
import zio._

final case class ConfirmModeLive(confirmCommandParser: ConfirmCommandParser, confirmView: ConfirmView)
  extends ConfirmMode {
  def process(input: String, state: State.Confirm): UIO[State] =
    confirmCommandParser
      .parse(input)
      .map {
        case ConfirmCommand.Yes => state.confirmed
        case ConfirmCommand.No  => state.declined
      }
      .orElseSucceed(state.copy(footerMessage = ConfirmFooterMessage.InvalidCommand))
  def render(state: State.Confirm): UIO[String] =
    for {
      header  <- confirmView.header(state.action)
      content <- confirmView.content
      footer  <- confirmView.footer(state.footerMessage)
    } yield List(header, content, footer).mkString("\n\n")
}
object ConfirmModeLive {
  val layer: URLayer[ConfirmCommandParser with ConfirmView, ConfirmMode] =
    ZLayer.fromFunction(ConfirmModeLive(_, _))
}