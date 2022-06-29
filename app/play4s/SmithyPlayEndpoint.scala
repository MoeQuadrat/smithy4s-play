package play4s

import akka.util.ByteString
import cats.data.EitherT
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  BodyParser,
  ControllerComponents,
  Handler,
  Request,
  RequestHeader,
  Result
}
import smithy4s.{Endpoint, HintMask, Interpreter}
import smithy4s.http.{
  BodyPartial,
  CodecAPI,
  HttpEndpoint,
  Metadata,
  PathParams,
  matchPath
}
import smithy4s.schema.Schema
import play.api.routing.Router.Routes
import cats.implicits._
import play.api.libs.json.{Format, JsError, JsValue, Json, Reads, Writes}
import play.api.libs.streams.{Accumulator, AkkaStreams}
import play4s.MyMonads.MyMonad
import smithy4s.internals.InputOutput

import scala.concurrent.{ExecutionContext, Future}

class SmithyPlayEndpoint[F[_] <: MyMonad[_], Op[
    _,
    _,
    _,
    _,
    _
], I, E, O, SI, SO](
    impl: Interpreter[Op, F],
    endpoint: Endpoint[Op, I, E, O, SI, SO],
    codecs: CodecAPI
)(implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc) {

  val inputSchema: Schema[I] = endpoint.input
  val inputMetadataDecoder =
    Metadata.PartialDecoder.fromSchema(inputSchema)

  def handler(v1: RequestHeader): Handler = {
    println("Endpoint")
    println(endpoint.name)
    HttpEndpoint
      .cast(endpoint)
      .map(httpEp => {
        Action.async { implicit request =>
          val result = for {
            pathParams <- EitherT(
              Future(
                httpEp
                  .matches(v1.path.replaceFirst("/", "").split("/"))
                  .toRight[Result](BadRequest("left"))
              )
            )
            metadata = getMetadata(pathParams, v1)
            _ = println(request.body)
            input <- EitherT(
              Future(inputMetadataDecoder.total match {
                case Some(value) => value.decode(metadata)
                case None =>
                  for {
                    metadataPartial <- inputMetadataDecoder.decode(metadata)
                    codec = codecs.compileCodec(inputSchema)
                    c <- codecs
                      .decodeFromByteArrayPartial(
                        codec,
                        Json.toBytes(request.body.asJson.get)
                      )
                      .leftMap(e => {
                        println(e)
                        BadRequest(e.toString())
                      })
                  } yield metadataPartial.combine(c)
              })
            ).leftMap(_ => BadRequest("Invalid Input Data"))

            //res <- (impl(endpoint.wrap(request.body)): F[O]).leftMap(_ =>
            res <- (impl(endpoint.wrap(input)): F[O]).leftMap(_ =>
              BadRequest("Invalid Input Data")
            )
          } yield Ok(Json.toJson(res._1)(res._2))
          result.value.map {
            case Left(value)  => value
            case Right(value) => value
          }
        }
      })
      .getOrElse(Action { NotFound("404") })
  }

  private def getMetadata(pathParams: PathParams, request: RequestHeader) =
    Metadata(
      path = pathParams,
      headers = getHeaders(request),
      query = request.queryString
        .map { case (k, v) => (k.trim, v) }
    )

}
