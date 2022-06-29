package playSmithy

import smithy4s.schema.Schema._

case class GetMenuRequest(id: String)
object GetMenuRequest extends smithy4s.ShapeTag.Companion[GetMenuRequest] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GetMenuRequest")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[GetMenuRequest] = struct(
    string.required[GetMenuRequest]("id", _.id).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
  ){
    GetMenuRequest.apply
  }.withId(id).addHints(hints)
}