package tictactoe.parser.game

import tictactoe.domain.{AppError, GameCommand}
import zio.{IO, ZIO}

trait GameCommandParser {
  def parse(input: String): IO[AppError, GameCommand]
}

object GameCommandParser {
  def parse(input: String) =
    ZIO.serviceWithZIO[GameCommandParser](_.parse(input))
}