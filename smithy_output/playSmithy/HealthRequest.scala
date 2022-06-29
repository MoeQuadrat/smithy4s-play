package playSmithy

import smithy4s.schema.Schema._

case class HealthRequest(query: Option[String] = None)
object HealthRequest extends smithy4s.ShapeTag.Companion[HealthRequest] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "HealthRequest")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[HealthRequest] = struct(
    string.optional[HealthRequest]("query", _.query).addHints(smithy.api.Length(Some(0), Some(5)), smithy.api.HttpQuery("query")).validated[smithy.api.Length, String],
  ){
    HealthRequest.apply
  }.withId(id).addHints(hints)
}