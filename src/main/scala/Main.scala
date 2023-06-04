import zhttp.service._
import zio._

object MainApp extends ZIOAppDefault {
  def run =
//    Server.start(port = 8080, http = GreetingApp() ++ StatusApp.app)
    Server.start(port = 8080, http = GreetingApp() ++ RequestApp() ++ StatusApp.app)
}