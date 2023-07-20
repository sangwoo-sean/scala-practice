package tictactoe.parser.confirm.game

import tictactoe.domain.{AppError, ConfirmCommand}
import zio.{IO, ZIO}

trait ConfirmCommandParser {
  def parse(input: String): IO[AppError, ConfirmCommand]
}

object ConfirmCommandParser {
  def parse(input: String) =
    ZIO.serviceWithZIO[ConfirmCommandParser](_.parse(input))
}