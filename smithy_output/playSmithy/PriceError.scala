package playSmithy

import smithy4s.schema.Schema._

case class PriceError(message: String) extends Throwable {
  override def getMessage() : String = message
}
object PriceError extends smithy4s.ShapeTag.Companion[PriceError] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "PriceError")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Error.CLIENT,
  )

  implicit val schema: smithy4s.Schema[PriceError] = struct(
    string.required[PriceError]("message", _.message).addHints(smithy.api.Required()),
  ){
    PriceError.apply
  }.withId(id).addHints(hints)
}