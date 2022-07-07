package play4s

import cats.data.EitherT
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Handler, Request, RequestHeader, Result, Results}
import smithy4s.{Endpoint, Interpreter}
import smithy4s.http.{CodecAPI, HttpEndpoint, Metadata, PathParams}
import smithy4s.schema.Schema
import cats.implicits._
import play.api.libs.json.{JsValue, Json}
import play4s.MyMonads.MyMonad

import scala.concurrent.{ExecutionContext, Future}

object SmithyPlayEndpoint {

  def apply[F[_] <: MyMonad[_], Op[_, _, _, _, _], I, E, O, SI, SO](
      impl: Interpreter[Op, F],
      endpoint: Endpoint[Op, I, E, O, SI, SO],
      codecs: CodecAPI
  )(implicit
      cc: ControllerComponents,
      ec: ExecutionContext
  ) = {
    new SmithyPlayEndpoint[F, Op, I, E, O, SI, SO](
      impl,
      endpoint,
      codecs
    )
  }

}

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

  val outputSchema: Schema[O] = endpoint.output

  private val outputMetadataEncoder =
    Metadata.Encoder.fromSchema(outputSchema)

  def handler(v1: RequestHeader): Handler = {
    HttpEndpoint
      .cast(endpoint)
      .map(httpEp => {
        Action.async { implicit request =>
          val result: EitherT[Future, MyErrorType, O] = for {
            pathParams <- getPathParams(v1, httpEp)
            metadata = getMetadata(pathParams, v1)
            input <- getInput(request, metadata)
            //res <- (impl(endpoint.wrap(request.body)): F[O]).leftMap(_ =>
            res <- (impl(endpoint.wrap(input)): F[O]).map {
              case o: O => o
            }
          } yield res
          result.value.map {
            case Left(value)  => Results.Status(value.statusCode)(value.message)
            case Right(value) => Ok(value)
          }
        }
      })
      .getOrElse(Action { NotFound("404") })
  }

  private def getPathParams(v1: RequestHeader, httpEp: HttpEndpoint[I]) = {
    EitherT(
      Future(
        httpEp
          .matches(v1.path.replaceFirst("/", "").split("/"))
          .toRight[MyErrorType](play4s.BadRequest("left"))
      )
    )
  }

  private def getInput(request: Request[AnyContent], metadata: Metadata) = {
    EitherT(
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
                play4s.BadRequest("left")
              })
          } yield metadataPartial.combine(c)
      })
    ).leftMap[MyErrorType](e => play4s.BadRequest("left"))
  }

  private def getMetadata(pathParams: PathParams, request: RequestHeader) =
    Metadata(
      path = pathParams,
      headers = getHeaders(request),
      query = request.queryString
        .map { case (k, v) => (k.trim, v) }
    )

  private def successResponse(output: O, httpEndpoint: HttpEndpoint[I]): Result = {
    val outputMetadata = outputMetadataEncoder.encode(output)
    val outputHeaders = outputMetadata.headers
    val successCode = httpEndpoint.code
    val codec = codecs.compileCodec(inputSchema)
    Results.Status(successCode)(output)
  }

}
