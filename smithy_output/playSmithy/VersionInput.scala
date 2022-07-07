package playSmithy

import smithy4s.ByteArray
import smithy4s.schema.Schema._

case class VersionInput(body: ByteArray)
object VersionInput extends smithy4s.ShapeTag.Companion[VersionInput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "VersionInput")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[VersionInput] = struct(
    bytes.required[VersionInput]("body", _.body).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    VersionInput.apply
  }.withId(id).addHints(hints)
}