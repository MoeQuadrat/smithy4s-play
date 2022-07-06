package play4s

import cats.data.EitherT
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Handler, Request, RequestHeader, Result, Results}
import smithy4s.{Endpoint, Interpreter}
import smithy4s.http.{CodecAPI, HttpEndpoint, Metadata, PathParams}
import smithy4s.schema.Schema
import cats.implicits._
import play.api.libs.json.Json
import play4s.MyMonads.MyMonad

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

  val httpEp = HttpEndpoint.cast(endpoint)

  def handler(v1: RequestHeader): Handler = {
    HttpEndpoint
      .cast(endpoint)
      .map(httpEp => {
        Action.async { implicit request =>
          val result = for {
            pathParams <- getPathParams(httpEp, v1)
            metadata = getMetadata(pathParams, v1)
            input <- getInput(metadata, request)
            res <- (impl(endpoint.wrap(input)): F[O]).leftMap(e =>
              Results.Status(e.statusCode)(e.message)
            )
          } yield Results.Status(httpEp.code)(Json.toJson(res._1)(res._2))
          result.value.map(_.merge)
        }
      })
      .getOrElse(Action { NotFound("404") })
  }

  def getPathParams(httpEndpoint: HttpEndpoint[I], header: RequestHeader) = EitherT(
    Future(
      httpEndpoint
        .matches(header.path.replaceFirst("/", "").split("/"))
        .toRight[Result](BadRequest("left"))
    )
  )

  def getInput(metadata: Metadata, request: Request[AnyContent]): EitherT[Future, Result, I] = EitherT(
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
  ).leftMap(e => BadRequest("Invalid Input Data"))


  private def getMetadata(pathParams: PathParams, request: RequestHeader) =
    Metadata(
      path = pathParams,
      headers = getHeaders(request),
      query = request.queryString
        .map { case (k, v) => (k.trim, v) }
    )

}
