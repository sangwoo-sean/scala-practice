import zhttp.http._

object StatusApp {
  val app: Http[Any, Nothing, Request, Response] = Http.collect[Request] {
    case Method.GET -> _ / "ok" => Response.ok
    case Method.GET -> _ / "redirect" => Response.redirect("ok")
  }
}
