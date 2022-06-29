package playSmithy

import smithy4s.schema.Schema._

sealed abstract class UnknownServerErrorCode(_value: String, _ordinal: Int) extends smithy4s.Enumeration.Value {
  override val value : String = _value
  override val ordinal: Int = _ordinal
  override val hints: smithy4s.Hints = smithy4s.Hints.empty
}
object UnknownServerErrorCode extends smithy4s.Enumeration[UnknownServerErrorCode] with smithy4s.ShapeTag.Companion[UnknownServerErrorCode] {
  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "UnknownServerErrorCode")

  val hints : smithy4s.Hints = smithy4s.Hints.empty

  case object ERROR_CODE extends UnknownServerErrorCode("server.error", 0)

  val values: List[UnknownServerErrorCode] = List(
    ERROR_CODE,
  )
  implicit val schema: smithy4s.Schema[UnknownServerErrorCode] = enumeration(values).withId(id).addHints(hints)
}