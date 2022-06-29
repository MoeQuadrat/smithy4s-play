package playSmithy

import smithy4s.schema.Schema._

case class Pizza(name: String, base: String, toppings: String)
object Pizza extends smithy4s.ShapeTag.Companion[Pizza] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Pizza")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Pizza] = struct(
    string.required[Pizza]("name", _.name).addHints(smithy.api.Required()),
    string.required[Pizza]("base", _.base).addHints(smithy.api.Required()),
    string.required[Pizza]("toppings", _.toppings).addHints(smithy.api.Required()),
  ){
    Pizza.apply
  }.withId(id).addHints(hints)
}