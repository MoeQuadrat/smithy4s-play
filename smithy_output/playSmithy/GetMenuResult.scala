package playSmithy

import smithy4s.schema.Schema._

case class GetMenuResult(item: MenuItem)
object GetMenuResult extends smithy4s.ShapeTag.Companion[GetMenuResult] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GetMenuResult")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[GetMenuResult] = struct(
    MenuItem.schema.required[GetMenuResult]("item", _.item).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    GetMenuResult.apply
  }.withId(id).addHints(hints)
}