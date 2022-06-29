package playSmithy

import smithy4s.schema.Schema._

case class FallbackError(error: String) extends Throwable {
}
object FallbackError extends smithy4s.ShapeTag.Companion[FallbackError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "FallbackError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.CLIENT,
  )

  implicit val schema: smithy4s.Schema[FallbackError] = struct(
    string.required[FallbackError]("error", _.error).addHints(smithy.api.Required()),
  ){
    FallbackError.apply
  }.withId(id).addHints(hints)
}