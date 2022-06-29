ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))

  .enablePlugins(Smithy4sCodegenPlugin, PlayScala)
  .settings(
    smithy4sInputDir in Compile  := (baseDirectory in ThisBuild).value / "smithy-in",
    smithy4sOutputDir in Compile := (baseDirectory in ThisBuild).value / "smithy_output",
    name := "smithy4s-play",
    scalaVersion := Dependencies.scalaVersion,
    libraryDependencies ++= Dependencies.list,
    libraryDependencies += guice

  )
