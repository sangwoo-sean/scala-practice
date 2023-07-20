package tictactoe.parser.menu

import tictactoe.domain.{AppError, MenuCommand}
import zio.{IO, ZIO}

trait MenuCommandParser {
  def parse(input: String): IO[AppError, MenuCommand]
}

object MenuCommandParser {
  def parse(input: String) =
    ZIO.serviceWithZIO[MenuCommandParser](_.parse(input))
}