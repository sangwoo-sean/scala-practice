package tictactoe.view.game

import tictactoe.domain.Board.Field
import tictactoe.domain._
import zio._

trait GameView {
  def header(result: GameResult, turn: Piece, player: Player): UIO[String]
  def content(board: Map[Field, Piece], result: GameResult): UIO[String]
  def footer(message: GameFooterMessage): UIO[String]
}

object GameView {
  def header(result: GameResult, turn: Piece, player: Player) =
    ZIO.serviceWithZIO[GameView](_.header(result, turn, player))
  def content(board: Map[Field, Piece], result: GameResult) =
    ZIO.serviceWithZIO[GameView](_.content(board, result))
  def footer(message: GameFooterMessage) =
    ZIO.serviceWithZIO[GameView](_.footer(message))
}