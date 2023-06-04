import zhttp.service._
import zio._

object MainApp extends ZIOAppDefault {
  def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server.start(
      port = 8080,
      http = GreetingApp() ++ RequestApp() ++ StatusApp()
    )
}