package tictactoe.opponentAi

import tictactoe.domain.Board.Field
import tictactoe.domain.Piece
import zio.{UIO, ZIO}

trait OpponentAi {
  def randomMove(board: Map[Field, Piece]): UIO[Field]
}

object OpponentAi {
  def randomMove(board: Map[Field, Piece]) =
    ZIO.serviceWithZIO[OpponentAi](_.randomMove(board))
}