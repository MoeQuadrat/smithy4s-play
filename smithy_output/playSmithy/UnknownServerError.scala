package playSmithy

import smithy4s.schema.Schema._

case class UnknownServerError(errorCode: UnknownServerErrorCode, description: Option[String] = None, stateHash: Option[String] = None) extends Throwable {
}
object UnknownServerError extends smithy4s.ShapeTag.Companion[UnknownServerError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "UnknownServerError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.SERVER,
    smithy.api.HttpError(500),
  )

  implicit val schema: smithy4s.Schema[UnknownServerError] = struct(
    UnknownServerErrorCode.schema.required[UnknownServerError]("errorCode", _.errorCode).addHints(smithy.api.Required()),
    string.optional[UnknownServerError]("description", _.description),
    string.optional[UnknownServerError]("stateHash", _.stateHash),
  ){
    UnknownServerError.apply
  }.withId(id).addHints(hints)
}