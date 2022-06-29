package play4s.meta

import smithy4s.Newtype
import smithy4s.schema.Schema._

object AdtMember extends Newtype[String] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("smithy4s.meta", "adtMember")
  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy.api.IdRef(Some("union"), Some(true), None),
    smithy.api.Documentation("adtMember trait can be added to structures that are targeted by\na single union. This trait tells smithy4s to generate the code\nsuch that the structure directly extends the union\'s sealed trait.\nThis makes it so the structure can be used directly was a member of\nthe union rather than being wrapped in a `MyStructureCase` class\nwhich is the default behavior.\nExample usage: @adtMember(MyUnion)"),
    smithy.api.Trait(Some("structure :not([trait|error])"), None, None),
  )
  val underlyingSchema : smithy4s.Schema[String] = string.withId(id).addHints(hints)
  implicit val schema : smithy4s.Schema[AdtMember] = bijection(underlyingSchema, AdtMember(_), (_ : AdtMember).value)
}