package playSmithy

import smithy4s.schema.Schema._

case class ByeRequest(test: String, bye: Bye)
object ByeRequest extends smithy4s.ShapeTag.Companion[ByeRequest] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "ByeRequest")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[ByeRequest] = struct(
    string.required[ByeRequest]("test", _.test).addHints(smithy.api.Required(), smithy.api.HttpLabel()),
    Bye.schema.required[ByeRequest]("bye", _.bye).addHints(smithy.api.Required(), smithy.api.HttpPayload()),
  ){
    ByeRequest.apply
  }.withId(id).addHints(hints)
}