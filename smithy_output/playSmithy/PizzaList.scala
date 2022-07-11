package playSmithy

import smithy4s.Newtype
import smithy4s.schema.Schema._

object PizzaList extends Newtype[List[MenuItem]] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "PizzaList")
  val hints : smithy4s.Hints = smithy4s.Hints.empty
  val underlyingSchema : smithy4s.Schema[List[MenuItem]] = list(MenuItem.schema).withId(id).addHints(hints)
  implicit val schema : smithy4s.Schema[PizzaList] = bijection(underlyingSchema, PizzaList(_), (_ : PizzaList).value)
}