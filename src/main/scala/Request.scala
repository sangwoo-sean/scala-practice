import zhttp.http._

object RequestApp {
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      case req @ (Method.GET -> _ / "request" / data) =>
        Response.text(
          s"${req.version}\n\n" +
            s"${req.method}\n\n" +

            s"${req.url}\n\n" +
            s"${req.url.path}\n\n" +
            s"${req.url.kind}\n\n" +
            s"${req.url.queryParams}\n\n" +
            s"${req.url.fragment}\n\n" +

            s"${req.headers}\n\n" +
            s"${req.headers.toChunk}\n\n" +

            s"${req.data}\n\n" +
            s"${req.path}\n\n" +
            s"${req.path.segments}\n\n"
        )
      case req @ (Method.GET -> _ / "request") =>
        Response.text(s"$req")
    }
}
