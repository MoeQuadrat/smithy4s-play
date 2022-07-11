package playSmithy

import smithy4s.schema.Schema._

case class GetAllRequest(items: List[MenuItem])
object GetAllRequest extends smithy4s.ShapeTag.Companion[GetAllRequest] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GetAllRequest")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[GetAllRequest] = struct(
    PizzaList.underlyingSchema.required[GetAllRequest]("items", _.items).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    GetAllRequest.apply
  }.withId(id).addHints(hints)
}