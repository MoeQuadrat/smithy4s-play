package playSmithy

import smithy4s.Document
import smithy4s.schema.Schema._

case class VersionOutput(version: Document)
object VersionOutput extends smithy4s.ShapeTag.Companion[VersionOutput] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "VersionOutput")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[VersionOutput] = struct(
    document.required[VersionOutput]("version", _.version).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    VersionOutput.apply
  }.withId(id).addHints(hints)
}