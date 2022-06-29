package playSmithy

import smithy4s.schema.Schema._

case class Hi(message: Option[String] = None)
object Hi extends smithy4s.ShapeTag.Companion[Hi] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Hi")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Hi] = struct(
    string.optional[Hi]("message", _.message),
  ){
    Hi.apply
  }.withId(id).addHints(hints)
}