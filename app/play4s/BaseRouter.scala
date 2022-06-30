package play4s

import play.api.mvc.{ControllerComponents, Handler, RequestHeader}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play4s.MyMonads.MyMonad
import smithy4s.Monadic

import scala.concurrent.ExecutionContext

abstract class BaseRouter(implicit
    controllerComponents: ControllerComponents,
    executionContext: ExecutionContext
) extends SimpleRouter {
  implicit def transformToRouter[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _], F[
      _
  ] <: MyMonad[_]](
      impl: Monadic[Alg, F]
  )(implicit serviceProvider: smithy4s.Service.Provider[Alg, Op]) = {
    new SmithyPlayRouter[Alg, Op, F](impl).routes()
  }

  def chain(
      toChain: Seq[Routes]
  ) = {
    println(toChain)
    toChain.foldLeft(PartialFunction.empty[RequestHeader, Handler])((a, b) =>
      a orElse b
    )
  }

  val controllers: Seq[Routes]

  lazy val chainedRoutes: Routes = chain(controllers)

  override def routes = chainedRoutes
}
