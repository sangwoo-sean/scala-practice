package tictactoe

import tictactoe.controller.{Controller, ControllerLive}
import tictactoe.domain.State
import tictactoe.domain.gameLogic.{GameLogic, GameLogicLive}
import tictactoe.mode.confirm.{ConfirmMode, ConfirmModeLive}
import tictactoe.mode.game.{GameMode, GameModeLive}
import tictactoe.mode.menu.{MenuMode, MenuModeLive}
import tictactoe.opponentAi.{OpponentAi, OpponentAiLive}
import tictactoe.parser.confirm.game.{ConfirmCommandParser, ConfirmCommandParserLive}
import tictactoe.parser.game.{GameCommandParser, GameCommandParserLive}
import tictactoe.parser.menu.{MenuCommandParser, MenuCommandParserLive}
import tictactoe.runLoop.{RunLoop, RunLoopLive}
import tictactoe.terminal.TerminalLive
import tictactoe.view.confirm.{ConfirmView, ConfirmViewLive}
import tictactoe.view.game.{GameView, GameViewLive}
import tictactoe.view.menu.{MenuView, MenuViewLive}
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

  val run = program.provide(environmentLayer)

  private lazy val environmentLayer: ULayer[RunLoop] = {

    val confirmModeDeps: ULayer[ConfirmCommandParser with ConfirmView] =
      ConfirmCommandParserLive.layer ++ ConfirmViewLive.layer
    val menuModeDeps: ULayer[MenuCommandParser with MenuView] =
      MenuCommandParserLive.layer ++ MenuViewLive.layer
    val gameModeDeps: ULayer[GameCommandParser with GameView with GameLogic with OpponentAi] =
      GameCommandParserLive.layer ++ GameViewLive.layer ++ GameLogicLive.layer ++ OpponentAiLive.layer

    val confirmModeNoDeps: ULayer[ConfirmMode] = confirmModeDeps >>> ConfirmModeLive.layer
    val menuModeNoDeps: ULayer[MenuMode] = menuModeDeps >>> MenuModeLive.layer
    val gameModeNoDeps: ULayer[GameMode] = gameModeDeps >>> GameModeLive.layer

    val controllerDeps: ULayer[ConfirmMode with GameMode with MenuMode] =
      confirmModeNoDeps ++ menuModeNoDeps ++ gameModeNoDeps

    val controllerNoDeps: ULayer[Controller] = controllerDeps >>> ControllerLive.layer

    val runLoopNoDeps = (controllerNoDeps ++ TerminalLive.layer) >>> RunLoopLive.layer

    runLoopNoDeps
  }
}
