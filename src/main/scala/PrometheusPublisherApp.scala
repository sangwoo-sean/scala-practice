import zio._
import zhttp.http._
import zio.metrics.connectors.prometheus.PrometheusPublisher
import zio.metrics.connectors.{prometheus, MetricsConfig}
import zhttp.service.Server
import zio.metrics.{Metric, MetricLabel}

object PrometheusPublisherApp {

  def apply(): Http[PrometheusPublisher, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> _ / "metrics" =>
        ZIO.serviceWithZIO[PrometheusPublisher](_.get.map(Response.text))
    }

}

object UserApp extends ZIOAppDefault {

  private val mc = ZLayer.succeed(MetricsConfig(1.seconds))

  def server: Http[Any, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      // GET /users
      case Method.GET -> _ / "users" =>
        ZIO.succeed(Response.json("""{"users": ["name": "sean"]}""")) @@
          Metric
            .counterInt("count_all_requests")
            .fromConst(1)
            .tagged(
              MetricLabel("method", "GET"),
              MetricLabel("handler", "users"),
            )
    }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .start(
        port = 8080,
        http = server ++ PrometheusPublisherApp()
      )
      .provide(mc, prometheus.publisherLayer, prometheus.prometheusLayer)

}
