package tictactoe

import tictactoe.controller.ControllerLive
import tictactoe.domain.State
import tictactoe.domain.gameLogic.GameLogicLive
import tictactoe.mode.confirm.ConfirmModeLive
import tictactoe.mode.game.GameModeLive
import tictactoe.mode.menu.MenuModeLive
import tictactoe.opponentAi.OpponentAiLive
import tictactoe.parser.confirm.game.ConfirmCommandParserLive
import tictactoe.parser.game.GameCommandParserLive
import tictactoe.parser.menu.MenuCommandParserLive
import tictactoe.runLoop.{RunLoop, RunLoopLive}
import tictactoe.terminal.TerminalLive
import tictactoe.view.confirm.ConfirmViewLive
import tictactoe.view.game.GameViewLive
import tictactoe.view.menu.MenuViewLive
import zio._

object TicTacToe extends ZIOAppDefault {

  val program: URIO[RunLoop, Unit] = {
    def loop(state: State): URIO[RunLoop, Unit] =
      RunLoop
        .step(state)
        .some
        .flatMap(loop)
        .ignore

    loop(State.initial)
  }

  val run =
    program
      .provide(
        ControllerLive.layer,
        GameLogicLive.layer,
        ConfirmModeLive.layer,
        GameModeLive.layer,
        MenuModeLive.layer,
        OpponentAiLive.layer,
        ConfirmCommandParserLive.layer,
        GameCommandParserLive.layer,
        MenuCommandParserLive.layer,
        RunLoopLive.layer,
        TerminalLive.layer,
        ConfirmViewLive.layer,
        GameViewLive.layer,
        MenuViewLive.layer
      )
}
