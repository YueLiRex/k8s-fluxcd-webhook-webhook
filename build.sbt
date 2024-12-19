import com.typesafe.sbt.packager.docker.{DockerChmodType, DockerPermissionStrategy, DockerVersion}

lazy val pekkoHttpVersion = "1.1.0"
lazy val pekkoVersion     = "1.1.2"
lazy val k8sClientVersion = "0.21.0"
lazy val circeVersion = "0.14.10"
lazy val PekkoHttpJsonVersion = "3.0.0"

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.github",
      scalaVersion    := "2.13.15"
    )),

    name := "webhook-server",
    version := "0.0.1",
    Compile / mainClass := Some("com.github.WebhookServer"),

    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-http"                % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-actor-typed"         % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream"              % pekkoVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.3.14",

      "dev.hnaderi" %% "scala-k8s-objects" % k8sClientVersion,
      "dev.hnaderi" %% "scala-k8s-client" % k8sClientVersion,
      "dev.hnaderi" %% "scala-k8s-circe" % k8sClientVersion,
      "com.github.pjfanning" %% "pekko-http-circe" % PekkoHttpJsonVersion,
      "com.chuusai" %% "shapeless" % "2.3.3",

      "org.apache.pekko" %% "pekko-http-testkit"        % pekkoHttpVersion % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.2.19"         % Test
    ),
    dockerSettings,
  ).enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val dockerSettings = Seq(
  dockerRepository := Option("ghcr.io/yuelirex"),
  dockerBaseImage := "ghcr.io/graalvm/graalvm-community:21.0.2",
  dockerPermissionStrategy := DockerPermissionStrategy.Run,
  dockerVersion := Some(DockerVersion(0, 0, 1, None)),
  Docker / packageName := "webhook-server",
  Docker / version := version.value,
  dockerExposedPorts ++= Seq(8080),
)
