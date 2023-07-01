ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "zio-tuto",
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"               % "2.0.15",
      "dev.zio"       %% "zio-test"          % "2.0.15" % Test,
      "dev.zio"       %% "zio-test-sbt"      % "2.0.15" % Test,
      "dev.zio"       %% "zio-test-magnolia" % "2.0.15" % Test,
      "io.d11"        %% "zhttp"             % "2.0.0-RC10",
      "com.lihaoyi"   %% "scalatags"         % "0.12.0",
      "io.getquill"   %% "quill-jdbc-zio"    % "4.6.1",
      "org.postgresql" % "postgresql"        % "42.5.4"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
