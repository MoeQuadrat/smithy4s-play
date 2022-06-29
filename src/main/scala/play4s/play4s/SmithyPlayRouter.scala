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

import cats.data.{Kleisli, OptionT}
import cats.effect.Concurrent
import cats.implicits._
import play.api.libs.json.OFormat
import play.api.mvc.{ActionBuilder, AnyContent, BaseController, Request}
import smithy4s.Interpreter
import smithy4s.http._
import play.api.mvc._
import play.api.routing._
import play.api.routing.sird._
import play.core.routing.{PathPart, PathPattern, Route}
import smithy4s.http._
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class SmithyPlayRouter[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _], F[_]](
    service: smithy4s.Service[Alg, Op],
    impl: Interpreter[Op, F],
    errorTransformation: PartialFunction[Throwable, F[Throwable]],
    codecs: OFormat[F[_]]
)(implicit ec: Concurrent[F], cc: ControllerComponents)
    extends AbstractController(cc) {

  val routes: Action[Request[F[_]]] = Action[Request[F], F] { request: Request[F[_]] =>
    for {
      endpoints <- perMethodEndpoint.get(request.method).toOptionT[F]
      path = matchPath.make(request.uri)
      (endpoint, pathParams) <- endpoints.collectFirstSome(_.matchTap(path)).toOptionT[F]
      res <- OptionT.liftF(endpoint.run(pathParams, request))
    } yield res
  }

  def routes(request: Request[F[_]]): Route.ParamsExtractor = Route(
    request.method,
    PathPattern(Seq(new PathPart { request.uri }))
  )

  private val httpEndpoints: List[SmithyPlayServerEndpoint[F]] =
    service.endpoints
      .map { ep =>
        SmithyPlayServerEndpoint(
          impl,
          ep,
          codecs,
          errorTransformation
        )
      }
      .collect { case Some(httpEndpoint) =>
        httpEndpoint
      }

  private val perMethodEndpoint
      : Map[String, List[SmithyPlayServerEndpoint[F]]] =
    httpEndpoints.groupBy(_.method)

}
