
package smithy4s.play4s

import cats.effect._
import cats.syntax.all._
import play.api.libs.json.Json
import play.api.mvc._
import smithy4s.{
  GenLift,
  Interpreter,
  Monadic,
  ShapeTag,
  UnsupportedProtocolError,
  checkProtocol
}

abstract class SimpleProtocolBuilder[P](implicit
    protocolTag: ShapeTag[P]
) {
  def routes[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _], F[_]](
      impl: Monadic[Alg, F]
  )(implicit
      serviceProvider: smithy4s.Service.Provider[Alg, Op],
      F: Concurrent[F],
      cc: ControllerComponents
  ): RouterBuilder[Alg, Op, F] = {
    val service = serviceProvider.service
    new RouterBuilder[Alg, Op, F](
      service,
      service.asTransformation[GenLift[F]#λ](impl),
      PartialFunction.empty
    )
  }

  class RouterBuilder[
      Alg[_[_, _, _, _, _]],
      Op[_, _, _, _, _],
      F[_]
  ](
      service: smithy4s.Service[Alg, Op],
      impl: Interpreter[Op, F],
      errorTransformation: PartialFunction[Throwable, F[Throwable]]
  )(implicit F: Concurrent[F], cc: ControllerComponents) {

    def make: Either[UnsupportedProtocolError, AnyRef] =
      checkProtocol(service, protocolTag).as {
        new SmithyPlayRouter[Alg, Op, F](
          service,
          impl,
          errorTransformation,
          Json.format[F[_]]
        ).routes
      }
  }

}
