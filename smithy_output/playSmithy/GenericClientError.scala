package playSmithy

import smithy4s.schema.Schema._

case class GenericClientError(message: String) extends Throwable {
  override def getMessage() : String = message
}
object GenericClientError extends smithy4s.ShapeTag.Companion[GenericClientError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GenericClientError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.CLIENT,
    smithy.api.HttpError(418),
  )

  implicit val schema: smithy4s.Schema[GenericClientError] = struct(
    string.required[GenericClientError]("message", _.message).addHints(smithy.api.Required()),
  ){
    GenericClientError.apply
  }.withId(id).addHints(hints)
}