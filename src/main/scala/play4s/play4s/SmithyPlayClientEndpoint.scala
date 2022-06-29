/*
 *  Copyright 2021 Disney Streaming
 *
 *  Licensed under the Tomorrow Open Source Technology License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     https://disneystreaming.github.io/TOST-1.0.txt
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package smithy4s.play4s

import cats.syntax.all._
import Compat.EffectCompat
import play.api.libs.json.{Json, OWrites}
import play.api.libs.typedmap.TypedMap
import play.api.mvc.request.{RemoteConnection, RequestTarget}
import play.api.mvc.{Headers, Request, RequestHeader, Result}
import play.libs.Json
import smithy4s.http._
import smithy4s.{Endpoint, Interpreter, Schema, errorTypeHeader}

/** A construct that encapsulates interprets and a low-level
  * client into a high-level, domain specific function.
  */
// format: off
 trait SmithyPlayClientEndpoint[F[_], Op[_, _, _, _, _], I, E, O, SI, SO] {
}
// format: on

object SmithyPlayClientEndpoint {

  def apply[F[_]: EffectCompat, Op[_, _, _, _, _], I, E, O, SI, SO](
      baseUri: String,
      endpoint: Endpoint[Op, I, E, O, SI, SO]
  ): Option[SmithyPlayClientEndpoint[F, Op, I, E, O, SI, SO]] =
    HttpEndpoint.cast(endpoint).map { httpEndpoint =>
      new SmithyPlayClientEndpointImpl[F, Op, I, E, O, SI, SO](
        baseUri,
        endpoint,
        httpEndpoint
      )
    }

}

// format: off
 class SmithyPlayClientEndpointImpl[F[_], Op[_, _, _, _, _], I, E, O, SI, SO](
                                                                               baseUri: String,
                                                                               endpoint: Endpoint[Op, I, E, O, SI, SO],
                                                                               httpEndpoint: HttpEndpoint[I],
)(implicit effect: EffectCompat[F]) extends SmithyPlayClientEndpoint[F, Op, I, E, O, SI, SO] {
// format: on

  private val method: String = toPlayMethod(httpEndpoint.method)

  private val inputSchema: Schema[I] = endpoint.input
  private val outputSchema: Schema[O] = endpoint.output

  private val inputMetadataEncoder =
    Metadata.Encoder.fromSchema(inputSchema)
  private val inputHasBody =
    Metadata.TotalDecoder.fromSchema(inputSchema).isEmpty
  private val outputMetadataDecoder =
    Metadata.PartialDecoder.fromSchema(outputSchema)

  def inputToRequest(input: I): Request[I] = {
    val metadata = inputMetadataEncoder.encode(input)
    val path = httpEndpoint.path(input)
    val uri = baseUri + path
    val header = toHeaders(metadata.headers)
    Request[I](
      new RequestHeader {
        override def connection: RemoteConnection = ???

        override def method: String = method

        override def target: RequestTarget = ???

        override def version: String = uri

        override def headers: Headers = header

        override def attrs: TypedMap = ???
      },
      input
    )

  }

  /*private def outputFromResponse(response: Result): F[O] =
    if (response.header.status < 300) outputFromSuccessResponse(response)
    else outputFromErrorResponse(response)

  private def outputFromSuccessResponse(response: Result): F[O] = {
    decodeResponse(response, outputMetadataDecoder).rethrow
  }

  private def errorResponseFallBack(response: Result): F[O] = {
    val headers = toMap(response.headers)
    val code = response.status.code
    response.as[String].flatMap { case body =>
      effect.raiseError(UnknownErrorResponse(code, headers, body))
    }
  }

  private def outputFromErrorResponse(response: Result): F[O] = {

    /** Find the error schema alternative that matches the errorTypeHeader.
   */
    def findErrorAltForHeader(err: Errorable[E]) = for {
      discriminator <- getFirstHeader(response, errorTypeHeader)
      oneOf <- err.error.alternatives.find(_.label == discriminator)
    } yield oneOf

    /** Attempt to decode for all union member, return as soon as one
   * decodes successfully.
   */
    def bestEffort(
        errorable: Errorable[E],
        alts: Vector[SchemaAlt[E, _]]
    ): F[O] = {
      alts
        .collectFirstSomeM { oneOf =>
          tryProcessError(errorable, oneOf, response)
            .map(_.toOption)
            .handleError { case _: PayloadError => None }
        }
        .flatMap {
          case Some(e) =>
            effect.raiseError(errorable.unliftError(e))
          case None =>
            errorResponseFallBack(response)
        }
    }

    endpoint.errorable match {
      case None => // can't do anything w/o the errorable
        errorResponseFallBack(response)
      case Some(errorable) =>
        val statusInt = response.status.code
        val errorAltPicker =
          new ErrorAltPicker(errorable.error.alternatives)
        // try to find precisely either by status code unique match
        // or by matching the errorTypeHeader
        val maybePreciseAlt =
          errorAltPicker
            .getPreciseAlternative(statusInt)
            .orElse(findErrorAltForHeader(errorable))

        val maybeError = maybePreciseAlt.map { alt =>
          processError(errorable, alt, response)
        }

        // if no response is rendered by then, do a best effort
        maybeError.getOrElse {
          val sortedAlts = errorAltPicker.orderedForStatus(statusInt)
          bestEffort(errorable, sortedAlts)
        }
    }
  }

  private def processError[ErrorType](
      errorable: Errorable[E],
      oneOf: SchemaAlt[E, ErrorType],
      response: Response[F]
  ): F[O] = {
    tryProcessError(errorable, oneOf, response).rethrow
      .map(errorable.unliftError)
      .flatMap(effect.raiseError)
  }

  private def tryProcessError[ErrorType](
      errorable: Errorable[E],
      oneOf: SchemaAlt[E, ErrorType],
      response: Response[F]
  ): F[Either[MetadataError, E]] = {
    val schema = oneOf.instance
    val errorMetadataDecoder = Metadata.PartialDecoder.fromSchema(schema)
    implicit val errorCodec = entityCompiler.compilePartialEntityDecoder(schema)
    decodeResponse[ErrorType](response, errorMetadataDecoder)
      .map(_.map(oneOf.inject))
  }

  private def decodeResponse[T](
      response: Result,
      metadataDecoder: Metadata.PartialDecoder[T]
  ): F[Either[MetadataError, T]] = {
    val headers = response.header.headers.map(h => (CaseInsensitive(h._1), Seq(h._2)))
    val metadata = Metadata(headers = headers)
    metadataDecoder.total match {
      case Some(totalDecoder) =>
        totalDecoder.decode(metadata).pure[F]
      case None =>
        for {
          metadataPartial <- metadataDecoder.decode(metadata).pure[F]
          bodyPartial = Json.parse(response.body.toString)
        } yield metadataPartial.map(_.combine(bodyPartial))
    }
  }*/
}
