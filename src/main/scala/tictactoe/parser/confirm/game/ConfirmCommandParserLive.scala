package tictactoe.parser.confirm.game

import tictactoe.domain.{AppError, ConfirmCommand, ParseError}
import zio._

final case class ConfirmCommandParserLive() extends ConfirmCommandParser {
  def parse(input: String): IO[AppError, ConfirmCommand] =
    input match {
      case "yes" => ZIO.succeed(ConfirmCommand.Yes)
      case "no"  => ZIO.succeed(ConfirmCommand.No)
      case _     => ZIO.fail(ParseError)
    }
}
object ConfirmCommandParserLive {
  val layer: ULayer[ConfirmCommandParser] = ZLayer.succeed(ConfirmCommandParserLive())
}