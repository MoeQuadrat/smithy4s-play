/*
package play4s.smithyExtension

import play.api.mvc.AbstractController
import smithy4s.Transformation

import scala.concurrent.{ExecutionContext, Future}

class PlayTransformer[F[_, _, _, _, _], G[_, _, _, _, _]] (implicit ec: ExecutionContext)  extends Transformation[F,G] with AbstractController { self =>
  override def apply[I, E, O, SI, SO](fa: F[I, E, O, SI, SO]): G[I, E, O, SI, SO] = Action.async(parse.anyContent) {
    Future.successful(super.apply(fa))
  }
}
*/
