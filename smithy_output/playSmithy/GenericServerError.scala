package playSmithy

import smithy4s.schema.Schema._

case class GenericServerError(message: String) extends Throwable {
  override def getMessage() : String = message
}
object GenericServerError extends smithy4s.ShapeTag.Companion[GenericServerError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GenericServerError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.SERVER,
    smithy.api.HttpError(502),
  )

  implicit val schema: smithy4s.Schema[GenericServerError] = struct(
    string.required[GenericServerError]("message", _.message).addHints(smithy.api.Required()),
  ){
    GenericServerError.apply
  }.withId(id).addHints(hints)
}