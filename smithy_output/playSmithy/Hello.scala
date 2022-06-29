package playSmithy

import smithy4s.schema.Schema._

case class Hello(message: Option[String] = None)
object Hello extends smithy4s.ShapeTag.Companion[Hello] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Hello")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  implicit val schema: smithy4s.Schema[Hello] = struct(
    string.optional[Hello]("message", _.message),
  ){
    Hello.apply
  }.withId(id).addHints(hints)
}