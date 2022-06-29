/*

package play4s.src.smithy4s.play4s

import play.api.http.MediaType
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import smithy4s.Schema
import smithy4s.http.{BodyPartial, CodecAPI, Metadata}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

trait JsonFormatter[F] {

  def jsonFormatter(schema: Schema[F]): OFormat[Class[F]]

}

object JsonFormatter {

  def fromCodecAPI[F](
                          codecAPI: CodecAPI
                        ): JsonFormatter[F] =
    (schema: Schema[F]) => {
      val codecA: codecAPI.Codec[F] = codecAPI.compileCodec(schema)
      Json.format[Class[F]]
    }
}
*/
