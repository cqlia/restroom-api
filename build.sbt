ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

inThisBuild(
  List(
    scalaVersion := "3.3.1",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

lazy val root = (project in file("."))
  .settings(
    name := "restroom-app",
    idePackagePrefix := Some("app")
  )

val zioVersion = "2.0.19"
val zioLoggingVersion = "2.1.15"
val zioConfigVersion = "4.0.0-RC16"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-http" % "3.0.0-RC3",
  "dev.zio" %% "zio-logging" % zioLoggingVersion,
  "dev.zio" %% "zio-logging-slf4j2-bridge" % zioLoggingVersion,
  "dev.zio" %% "zio-json" % "0.6.2",
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "io.getquill" %% "quill-jdbc-zio" % "4.8.0",
  "org.postgresql" % "postgresql" % "42.6.0",
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC11" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    (xs map {_.toLowerCase}) match {
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.discard
    }
  case x => MergeStrategy.first
}

