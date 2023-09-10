package sttp

import sttp.capabilities.zio.ZioStreams
import sttp.client4._
import sttp.client4.httpclient.zio.HttpClientZioBackend
import sttp.client4.testing.WebSocketStreamBackendStub
import sttp.model.headers.Cookie
import sttp.model.{Header, Method, StatusCode, Uri}
import zio.{Scope, Task, ZIO}
import zio.test.{assertTrue, Spec, TestEnvironment, ZIOSpecDefault}

object TestingBackend {

  def server: WebSocketStreamBackendStub[Task, ZioStreams] =
    HttpClientZioBackend.stub
      .whenRequestMatchesPartial {
        case Request(Method.GET, uri: Uri, _, _, _, _, _) if uri.path.startsWith(List("foo", "bar")) =>
          Response("Hello there!", StatusCode.Ok)
        case Request(Method.POST, uri, _, _, _, _, _) if uri.path == List("error", "client") =>
          Response("", StatusCode.BadRequest)
        case Request(Method.GET, uri, _, _, _, _, _) if uri.path == List("error", "server") =>
          Response("", StatusCode.InternalServerError)
        case Request(Method.POST, uri, body, _, _, _, _) if uri.path == List("body") =>
          println(body)
          Response("this is response", StatusCode.Ok)
        case Request(Method.POST, uri, _, headers: Seq[Header], _, _, _) if uri.path == List("cookie") =>
          headers.foreach(x => println(x))
          Response("this is cookie", StatusCode.Ok, "", Seq(Header.cookie(Cookie("logout", "you"))))
        case Request(Method.POST, uri, _, headers, _, _, _) if uri.path == List("auth") =>
          val statusCode =
            if (headers.contains(Header.authorization("Bearer", "zMDjRfl76ZC9Ub0wnz4XsNiRVBChTYbJcE3F"))) {
              StatusCode.Ok
            } else {
              StatusCode.Unauthorized
            }
          Response("authorized", statusCode)
      }

}

object SttpSpec extends ZIOSpecDefault {
  val baseUrl = "http://example.org"

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("sttp")(
      test("get with path") {
        for {
          response <- basicRequest
            .get(uri"$baseUrl/foo/bar/baz")
            .send(TestingBackend.server)
        } yield assertTrue(response.body.map(_ == "Hello there!").getOrElse(false))
      },
      test("post, client error") {
        for {
          response <- basicRequest
            .post(uri"$baseUrl/error/client")
            .send(TestingBackend.server)
        } yield assertTrue(response.isClientError)
      },
      test("get, server error") {
        for {
          response <- basicRequest
            .get(uri"$baseUrl/error/server")
            .send(TestingBackend.server)
        } yield assertTrue(response.isServerError)
      },
      test("post, with body") {
        for {
          response <- basicRequest
            .body("this is body")
            .post(uri"$baseUrl/body")
            .send(TestingBackend.server)
        } yield assertTrue(response.body.map(_ == "this is response").getOrElse(false))
      },
      test("post, with cookie") {
        for {
          response <- basicRequest
            .cookie("login", "me")
            .post(uri"$baseUrl/cookie")
            .send(TestingBackend.server)
        } yield assertTrue(
          response.body.map(_ == "this is cookie").getOrElse(false) &&
            response.headers.head == Header("Cookie", "logout=you")
        )
      },
      test("auth") {
        for {
          _ <- ZIO.unit
          token = "zMDjRfl76ZC9Ub0wnz4XsNiRVBChTYbJcE3F"
          response <- basicRequest.auth
            .bearer(token)
            .post(uri"$baseUrl/auth")
            .send(TestingBackend.server)
        } yield assertTrue(response.isSuccess)
      },
    )

}
