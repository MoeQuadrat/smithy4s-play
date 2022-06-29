package smithy4s.play4s

import cats.data.Kleisli
import cats.effect.Concurrent
import cats.implicits.catsSyntaxApplicativeError
import cats.syntax.all._
import play.api.http.{HttpEntity, Status}
import play.api.libs.json.OFormat
import play.api.mvc.{Action, Headers, Request, ResponseHeader, Result}
import play.http.websocket.Message
import smithy4s.http._
import smithy4s.{Endpoint, Interpreter, Schema, errorTypeHeader}

trait SmithyPlayServerEndpoint[F[_]] {
  def method: String
  def matches(path: Array[String]): Option[PathParams]
  def run(pathParams: PathParams, request: Request[F[_]]): F[Result]

  def matchTap(
      path: Array[String]
  ): Option[(SmithyPlayServerEndpoint[F], PathParams)] =
    matches(path).map(this -> _)
}

object SmithyPlayServerEndpoint {
  def apply[F[_]: Concurrent, Op[_, _, _, _, _], I, E, O, SI, SO](
      impl: Interpreter[Op, F],
      endpoint: Endpoint[Op, I, E, O, SI, SO],
      codecs: OFormat[F[_]],
      errorTransformation: PartialFunction[Throwable, F[Throwable]]
  ): Option[SmithyPlayServerEndpoint[F]] =
    HttpEndpoint.cast(endpoint).map { httpEndpoint =>
      new SmithyPlayServerEndpointImpl[F, Op, I, E, O, SI, SO](
        impl,
        endpoint,
        httpEndpoint,
        codecs,
        errorTransformation
      )
    }
}

class SmithyPlayServerEndpointImpl[F[_], Op[_, _, _, _, _], I, E, O, SI, SO](
    impl: Interpreter[Op, F],
    endpoint: Endpoint[Op, I, E, O, SI, SO],
    httpEndpoint: HttpEndpoint[I],
    codecs: OFormat[F[_]],
    errorTransformation: PartialFunction[Throwable, F[Throwable]]
)(implicit F: Concurrent[F])
    extends SmithyPlayServerEndpoint[F] {

  type ==>[A, B] = Kleisli[F, A, B]

  override def method: String = toPlayMethod(httpEndpoint.method)

  override def matches(path: Array[String]): Option[PathParams] =
    httpEndpoint.matches(path)

  def run(pathParams: PathParams, request: Request[F[_]]): F[Result] = {
    val run: F[O] = for {
      metadata <- getMetadata(pathParams, request)
      input <- extractInput.run((metadata, request))
      output <- (impl(endpoint.wrap(input)): F[O])
    } yield output

    run.recoverWith(transformError).attempt.flatMap {
      case Left(error)   => errorResponse(error)
      case Right(output) => successResponse(output)
    }
  }

  private val inputSchema: Schema[I] = endpoint.input
  private val outputSchema: Schema[O] = endpoint.output

  private val inputMetadataDecoder =
    Metadata.PartialDecoder.fromSchema(inputSchema)
  private val outputMetadataEncoder =
    Metadata.Encoder.fromSchema(outputSchema)

  private val transformError: PartialFunction[Throwable, F[O]] = {
    case e @ endpoint.Error(_, _) => F.raiseError(e)
    case scala.util.control.NonFatal(other)
        if errorTransformation.isDefinedAt(other) =>
      errorTransformation(other).flatMap(F.raiseError)
  }

  // format: off
  private val extractInput: (Metadata, Request[F[_]]) ==> I = {
    inputMetadataDecoder.total match {
      case Some(totalDecoder) =>
        Kleisli(totalDecoder.decode(_: Metadata).liftTo[F]).local(_._1)
      /*case None =>
        // NB : only compiling the input codec if the data cannot be
        // totally extracted from the metadata.
        implicit val inputCodec = codecs.compilePartialEntityDecoder(inputSchema)
        Kleisli { case (metadata, request) =>
          for {
            metadataPartial <- inputMetadataDecoder.decode(metadata).liftTo[F]
            bodyPartial <- request.as[BodyPartial[I]]
          } yield metadataPartial.combine(bodyPartial)
        }*/
    }
  }
  // format: on

  private def getMetadata(pathParams: PathParams, request: Request[F[_]]) =
    Metadata(
      path = pathParams,
      headers = getHeaders(request),
      query = request.queryString
        .map { case (k, v) => (k.trim, v) }
    ).pure[F]

  private def successResponse(output: O): F[Result] = {
    val outputMetadata = outputMetadataEncoder.encode(output)
    val outputHeaders = toHeaders(outputMetadata.headers)
    val successCode = httpEndpoint.code
    Result(
      ResponseHeader(successCode, outputHeaders.toSimpleMap),
      HttpEntity.NoEntity,
      None,
      None,
      Seq.empty
    ).pure[F]
  }

  private def errorResponse(throwable: Throwable): F[Result] = {

    def errorHeaders(errorLabel: String, metadata: Metadata): Headers =
      toHeaders(metadata.headers)
        .add(
          (errorTypeHeader, errorLabel)
        )

    /*def processAlternative[ErrorUnion, ErrorType](
                                                   altAndValue: Alt.SchemaAndValue[ErrorUnion, ErrorType]
                                                 ): Result = {
      val errorSchema = altAndValue.alt.instance
      val errorValue = altAndValue.value
      val errorCode =
        http.HttpStatusCode.fromSchema(errorSchema).code(errorValue, 500)
      implicit val errorCodec = codecs.compileEntityEncoder(errorSchema)
      val metadataEncoder = Metadata.Encoder.fromSchema(errorSchema)
      val metadata = metadataEncoder.encode(errorValue)
      val headers = errorHeaders(altAndValue.alt.label, metadata)
      val status =
        Status.fromInt(errorCode).getOrElse(Status.InternalServerError)
      Response(status, headers = headers).withEntity(errorValue)
    }*/

    throwable
      .pure[F]
      .flatMap {
        case e: HttpContractError =>
          Result(
            ResponseHeader(500, errorHeaders("500", Metadata()).toSimpleMap),
            HttpEntity.NoEntity,
            None,
            None,
            Seq.empty
          ).pure[F]
        case e: Throwable =>
          F.raiseError(e)
      }
  }
}
