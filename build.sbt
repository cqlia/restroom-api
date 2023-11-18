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
    name := "bathroom-app",
    idePackagePrefix := Some("app")
  )

val zioVersion = "2.0.19"
val zioConfigVersion = "4.0.0-RC16"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-http" % "3.0.0-RC3",
  "dev.zio" %% "zio-logging" % "2.1.15",
  "dev.zio" %% "zio-json" % "0.6.2",
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
