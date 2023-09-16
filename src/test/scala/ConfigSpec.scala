import zio._
import zio.config.magnolia.DeriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.test._

case class MyConfig(value: Option[String], value2: Option[String], value3: Option[String], nothing: Option[String])

object MyConfig {
  val config: Config[MyConfig] = DeriveConfig.deriveConfig[MyConfig].nested("my", "env")
}

object ConfigSpec extends ZIOSpecDefault {

  val testConfigProviderFromMap: ZLayer[Any, Nothing, Unit] =
    Runtime.setConfigProvider(
      ConfigProvider
        .fromMap(Map("my.env.value" -> "this is from map")))

  private val configProvider: ConfigProvider = TypesafeConfigProvider
    .fromHoconFilePath("conf/application.conf")
    .orElse(TypesafeConfigProvider.fromResourcePath())

  val testConfigProviderFromConfFile: ZLayer[Any, Nothing, Unit] =
    Runtime.setConfigProvider(configProvider)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("config from map")(
      test("fetch value from map") {
        for {
          config <- ZIO.config[MyConfig](MyConfig.config)
        } yield assertTrue(config.value.contains("this is from map"))
      }
    ).provideLayer(testConfigProviderFromMap) +
      suite("config from conf file")(
        test("fetch value from  conf/application.conf") {
          for {
            config <- ZIO.config[MyConfig](MyConfig.config)
          } yield assertTrue(config.value.contains("I am a value from conf/application.conf"))
        },
        test("fetch value from test/resources/application.conf") {
          for {
            config <- ZIO.config[MyConfig](MyConfig.config)
          } yield assertTrue(config.value2.contains("I am a value from test/resources/application.conf"))
        },
        test("fetch value from main/resources/application.conf") {
          for {
            config <- ZIO.config[MyConfig](MyConfig.config)
          } yield assertTrue(config.value3.contains("I am a value from main/resources/application.conf"))
        },
        test("fetch value doesn't exist") {
          for {
            config <- ZIO.config[MyConfig](MyConfig.config)
          } yield assertTrue(config.nothing.isEmpty)
        },
      ).provideLayer(testConfigProviderFromConfFile)

}
