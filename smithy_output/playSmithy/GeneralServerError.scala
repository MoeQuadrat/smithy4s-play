package playSmithy

import smithy4s.schema.Schema._

case class GeneralServerError(message: Option[String] = None) extends Throwable {
  override def getMessage() : String = message.orNull
}
object GeneralServerError extends smithy4s.ShapeTag.Companion[GeneralServerError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GeneralServerError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.SERVER,
    smithy.api.HttpError(500),
  )

  implicit val schema: smithy4s.Schema[GeneralServerError] = struct(
    string.optional[GeneralServerError]("message", _.message),
  ){
    GeneralServerError.apply
  }.withId(id).addHints(hints)
}