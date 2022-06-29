package playSmithy

import smithy4s.schema.Schema._

case class MenuItem(food: Pizza, price: Float, id: Option[String] = None, added: Option[String] = None)
object MenuItem extends smithy4s.ShapeTag.Companion[MenuItem] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "MenuItem")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[MenuItem] = struct(
    Pizza.schema.required[MenuItem]("food", _.food).addHints(smithy.api.Required()),
    float.required[MenuItem]("price", _.price).addHints(smithy.api.Required()),
    string.optional[MenuItem]("id", _.id),
    string.optional[MenuItem]("added", _.added),
  ){
    MenuItem.apply
  }.withId(id).addHints(hints)
}