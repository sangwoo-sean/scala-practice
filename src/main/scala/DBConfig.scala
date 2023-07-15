import zio.config.magnolia.deriveConfig

case class DBConfig(
    serverName: String,
    portNumber: Int,
    user: String,
    password: String,
    databaseName: String
)

object DBConfig {
  val config = deriveConfig[DBConfig].nested("myDatabaseConfig", "dataSource")
}
