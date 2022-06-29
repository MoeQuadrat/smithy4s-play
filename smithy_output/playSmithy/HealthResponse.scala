package playSmithy

import smithy4s.schema.Schema._

case class HealthResponse(status: String)
object HealthResponse extends smithy4s.ShapeTag.Companion[HealthResponse] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "HealthResponse")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[HealthResponse] = struct(
    string.required[HealthResponse]("status", _.status).addHints(smithy.api.Required()),
  ){
    HealthResponse.apply
  }.withId(id).addHints(hints)
}