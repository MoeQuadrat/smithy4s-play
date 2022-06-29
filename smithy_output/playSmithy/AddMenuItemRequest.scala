package playSmithy

import smithy4s.schema.Schema._

case class AddMenuItemRequest(menuItem: MenuItem)
object AddMenuItemRequest extends smithy4s.ShapeTag.Companion[AddMenuItemRequest] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "AddMenuItemRequest")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[AddMenuItemRequest] = struct(
    MenuItem.schema.required[AddMenuItemRequest]("menuItem", _.menuItem).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    AddMenuItemRequest.apply
  }.withId(id).addHints(hints)
}