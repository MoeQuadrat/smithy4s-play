package playSmithy

import smithy4s.schema.Schema._

case class Bye(message: String)
object Bye extends smithy4s.ShapeTag.Companion[Bye] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Bye")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Bye] = struct(
    string.required[Bye]("message", _.message).addHints(smithy.api.Required()),
  ){
    Bye.apply
  }.withId(id).addHints(hints)
}