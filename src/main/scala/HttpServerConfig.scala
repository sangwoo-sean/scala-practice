import zio.config.magnolia.deriveConfig

case class HttpServerConfig(port: String, host: String, nThreads: Int)

object HttpServerConfig {
  val config = deriveConfig[HttpServerConfig].nested("HttpServerConfig")
}