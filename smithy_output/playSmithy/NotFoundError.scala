package playSmithy

import smithy4s.schema.Schema._

case class NotFoundError(name: String) extends Throwable {
}
object NotFoundError extends smithy4s.ShapeTag.Companion[NotFoundError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "NotFoundError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.CLIENT,
    smithy.api.HttpError(404),
  )

  implicit val schema: smithy4s.Schema[NotFoundError] = struct(
    string.required[NotFoundError]("name", _.name).addHints(smithy.api.Required()),
  ){
    NotFoundError.apply
  }.withId(id).addHints(hints)
}