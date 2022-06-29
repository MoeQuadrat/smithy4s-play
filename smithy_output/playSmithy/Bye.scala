package playSmithy

import smithy4s.schema.Schema._

case class Bye(message: Option[String] = None)
object Bye extends smithy4s.ShapeTag.Companion[Bye] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Bye")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Bye] = struct(
    string.optional[Bye]("message", _.message),
  ){
    Bye.apply
  }.withId(id).addHints(hints)
}