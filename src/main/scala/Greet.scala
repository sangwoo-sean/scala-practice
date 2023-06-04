import zhttp.http._

/** An http app that:
 *   - Accepts a `Request` and returns a `Response`
 *   - Does not fail
 *   - Does not use the environment
 */
object GreetingApp {
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collect[Request] {
      // GET /greet?name=:name
      case req@(Method.GET -> _ / "greet")
        if (req.url.queryParams.nonEmpty) =>
        Response.text(s"Hello ${req.url.queryParams("name").mkString(" and ")}!")

      // GET /greet
      case Method.GET -> _ / "greet" =>
        Response.text(s"Hello World!")

      // GET /greet/:name
      case Method.GET -> _ / "greet" / name =>
        Response.text(s"Hello $name!")

      case Method.GET -> _ / "fruits" / "a" =>
        Response.text("Apple")

      case Method.GET -> _ / "fruits" / "b" =>
        Response.text("banana")

      case Method.GET -> _ / "Apple" / int(count) =>
        Response.text(s"Apple: $count")
    }
}