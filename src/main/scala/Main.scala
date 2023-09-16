import Koreaexim._
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zhttp.service._
import zio._
import zio.config.magnolia.DeriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.backend.SLF4J

case class MyConfig(value: String)

object MyConfig {
  val config: Config[MyConfig] = DeriveConfig.deriveConfig[MyConfig].nested("my", "env")
}

object Main extends ZIOAppDefault {

  private val configProvider: ConfigProvider =
    TypesafeConfigProvider
      .fromHoconFilePath("conf/application.conf")
      .kebabCase
      .orElse(TypesafeConfigProvider.fromResourcePath())

  override val bootstrap: ZLayer[Any, Any, Unit] =
    Runtime.removeDefaultLoggers >>>
      SLF4J.slf4j >>>
      Runtime.setConfigProvider(configProvider)

  val prog = for {
    _ <- ZIO.logInfo("Start")

    serverConfig <- ZIO.config[HttpServerConfig](HttpServerConfig.config).debug
    dbConfig     <- ZIO.config[DBConfig](DBConfig.config).debug
    myconfig     <- ZIO.config[MyConfig](MyConfig.config).debug("myconfig")

    _                  <- ZIO.logInfo("It's Scheduling!").repeat(Schedule.fixed(Duration.fromSeconds(60)).forever).fork
    migrator           <- ZIO.service[DbMigrator]
    _                  <- migrator.migrate
    personEndPoint     <- ZIO.service[PersonEndPoint]
    koreaeximEndpoints <- ZIO.service[KoreaeximEndpoints]
    _                  <- Server.start(serverConfig.port, personEndPoint.endpoint ++ koreaeximEndpoints.endpoint)
    _                  <- ZIO.logInfo("Done")
  } yield ()

  override def run =
    prog.provide(
      DbMigrator.live,
      PersonEndPoint.live,
      KoreaeximEndpoints.live,
      QuillPersonRepository.live,
      Quill.Postgres.fromNamingStrategy(SnakeCase),
      Quill.DataSource.fromPrefix("myDatabaseConfig")
    )

}
