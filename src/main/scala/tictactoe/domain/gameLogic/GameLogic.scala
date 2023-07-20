package tictactoe.domain.gameLogic

import tictactoe.domain.Board.Field
import tictactoe.domain.{AppError, GameResult, Piece}
import zio._

trait GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece): IO[AppError, Map[Field, Piece]]
  def gameResult(board: Map[Field, Piece]): UIO[GameResult]
  def nextTurn(currentTurn: Piece): UIO[Piece]
}

object GameLogic {
  def putPiece(board: Map[Field, Piece], field: Field, piece: Piece) =
    ZIO.serviceWithZIO[GameLogic](_.putPiece(board, field, piece))
  def gameResult(board: Map[Field, Piece]) =
    ZIO.serviceWithZIO[GameLogic](_.gameResult(board))
  def nextTurn(currentTurn: Piece) =
    ZIO.serviceWithZIO[GameLogic](_.nextTurn(currentTurn))
}