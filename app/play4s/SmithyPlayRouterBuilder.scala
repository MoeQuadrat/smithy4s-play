package play4s

import play.api.mvc.{ControllerComponents, Handler, RequestHeader}
import play.api.routing.Router.Routes
import play4s.MyMonads.MyMonad
import smithy4s.{GenLift, HintMask, Monadic}
import smithy4s.http.HttpEndpoint
import smithy4s.internals.InputOutput

import scala.concurrent.ExecutionContext

class SmithyPlayRouterBuilder[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _], F[
    _
] <: MyMonad[_]](
    impl: Seq[Monadic[Alg, F]],
    service: Seq[smithy4s.Service.Provider[Alg, Op]]
)(implicit cc: ControllerComponents, ec: ExecutionContext) {

  def getRoutes(): Routes = {
    val routers = impl.zip(service).map(
      z => new SmithyPlayRouter(z._1).routes()(z._2)
    )
    new PartialFunction[RequestHeader, Handler] {
      override def isDefinedAt(x: RequestHeader): Boolean = routers.foldLeft(routers.head.isDefinedAt(x))((acc, routes) => if(acc) acc else routes.isDefinedAt(x))

      override def apply(v1: RequestHeader): Handler = routers.foldLeft(routers.head)((acc, routes) => if(acc.isDefinedAt(v1)) acc else routes).apply(v1)
    }
  }
}
