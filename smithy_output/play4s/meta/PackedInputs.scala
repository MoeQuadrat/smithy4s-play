package play4s.meta

import smithy4s.schema.Schema._

case class PackedInputs()
object PackedInputs extends smithy4s.ShapeTag.Companion[PackedInputs] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.meta", "packedInputs")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.Trait(Some(":is(service, operation)"), None, None),
  )

  implicit val schema: smithy4s.Schema[PackedInputs] = constant(PackedInputs()).withId(id).addHints(hints)
}