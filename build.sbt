ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val tapirVersion     = "1.6.0"
val http4sVersion    = "0.23.18"
val DoobieVersion    = "1.0.0-RC1"
val NewTypeVersion   = "0.4.4"
val zioSchemaVersion = "1.0.1"

lazy val root = (project in file("."))
  .settings(
    name := "zio-tuto",
    libraryDependencies ++= Seq(
      "dev.zio"                     %% "zio"                     % "2.0.17",
      "dev.zio"                     %% "zio-test"                % "2.0.17" % Test,
      "dev.zio"                     %% "zio-test-sbt"            % "2.0.17" % Test,
      "dev.zio"                     %% "zio-test-magnolia"       % "2.0.17" % Test,
      "org.scalatest"               %% "scalatest"               % "3.2.15" % Test,
      "dev.zio"                     %% "zio-config"              % "4.0.0-RC14",
      "dev.zio"                     %% "zio-config-typesafe"     % "4.0.0-RC14",
      "dev.zio"                     %% "zio-config-magnolia"     % "4.0.0-RC14",
      "io.d11"                      %% "zhttp"                   % "2.0.0-RC10",
      "com.lihaoyi"                 %% "scalatags"               % "0.12.0",
      "io.getquill"                 %% "quill-jdbc-zio"          % "4.6.1",
      "org.postgresql"               % "postgresql"              % "42.5.4",
      "dev.zio"                     %% "zio-json"                % "0.6.1",
      "dev.zio"                     %% "zio-json-macros"         % "0.5.0",
      "ch.qos.logback"               % "logback-classic"         % "1.4.7",
      "dev.zio"                     %% "zio-logging-slf4j2"      % "2.1.13",
      "org.flywaydb"                 % "flyway-core"             % "9.19.4",
      "org.tpolecat"                %% "atto-core"               % "0.9.5",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.6.4",
      // tapir
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"        % tapirVersion,
      // doobie
      "org.tpolecat" %% "doobie-core"     % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari"   % DoobieVersion,
      "io.estatico"  %% "newtype"         % NewTypeVersion,
      // http4s
      "org.http4s" %% "http4s-blaze-server" % "0.23.13",
      "org.http4s" %% "http4s-blaze-client" % "0.23.13",
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      // sttp
      "com.softwaremill.sttp.client4" %% "zio" % "4.0.0-M4",
      // ZIO Prelude
      "dev.zio" %% "zio-prelude" % "1.0.0-RC21",
      // circe (CATS)
      "io.circe" %% "circe-core"    % "0.14.5",
      "io.circe" %% "circe-generic" % "0.14.5",
      "io.circe" %% "circe-parser"  % "0.14.5",
      // ZIO schema
      "dev.zio" %% "zio-schema"            % zioSchemaVersion,
      "dev.zio" %% "zio-schema-json"       % zioSchemaVersion,
      "dev.zio" %% "zio-schema-derivation" % zioSchemaVersion,
      // metrics
      "dev.zio" %% "zio-metrics-connectors"            % "2.2.0",
      "dev.zio" %% "zio-metrics-connectors-prometheus" % "2.2.0",
      "dev.zio" %% "zio-macros"                        % "2.0.17",
      //
      "dev.zio" %% "zio-http"      % "3.0.0-RC4",
    ),
    scalacOptions += "-Ymacro-annotations",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
