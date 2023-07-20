package tictactoe.domain

sealed trait AppError
case object ParseError                extends AppError
case object FieldAlreadyOccupiedError extends AppError