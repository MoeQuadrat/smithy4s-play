package play4s

import cats.data.EitherT
import play.api.mvc.{
  AbstractController,
  ControllerComponents,
  Handler,
  RequestHeader,
  Result
}
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

  def handler(v1: RequestHeader): Handler = {
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
            ).leftMap(e => BadRequest("Invalid Input Data"))

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

  /*private def successResponse(output: O): MyMonad[O] = {
    val outputMetadata = outputMetadataEncoder.encode(output)
    val outputHeaders = toHeaders(outputMetadata.headers)
    val successCode = status(httpEndpoint.code)
    putHeaders(Response[F](successCode), outputHeaders)
      .withEntity(output)
      .pure[F]
  }*/

  private def getMetadata(pathParams: PathParams, request: RequestHeader) =
    Metadata(
      path = pathParams,
      headers = getHeaders(request),
      query = request.queryString
        .map { case (k, v) => (k.trim, v) }
    )

}
