import sbt._

object Dependencies {

  val scalaVersion = "2.13.8"

  val smithyCore = "com.disneystreaming.smithy4s" %% "smithy4s-core" % "0.13.5"
  val smithyJson = "com.disneystreaming.smithy4s" %% "smithy4s-json" % "0.13.5"
  val smithyTests = "com.disneystreaming.smithy4s" %% "smithy4s-tests" % "0.13.5"
  val scalatestPlus =
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
  val cats = "org.typelevel" %% "cats-core" % "2.7.0"
  val catsEffect3 =
    "org.typelevel" %% "cats-effect" % "3.3.12"


  lazy val list = Seq(
    smithyCore,
    smithyJson,
    scalatestPlus,
    smithyTests,
    cats,
    catsEffect3
  )

}
