/*

package play4s.src.smithy4s.play4s

import play4s.src.smithy4s.play4s.Compat.EffectCompat
import smithy4s.{Endpoint, Interpreter, Transformation}

// format: off
class SmithyPlayReverseRouter[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _], F[_]](
    baseUri: String,
    service: smithy4s.Service[Alg, Op],
)(implicit effect: EffectCompat[F])
    extends Interpreter[Op, F] {
// format: on

  def apply[I, E, O, SI, SO](
      op: Op[I, E, O, SI, SO]
  ): F[O] = {
    val (input, endpoint) = service.endpoint(op)
    val http4sEndpoint = clientEndpoints(endpoint)
    http4sEndpoint.send(input)
  }

  private val clientEndpoints =
    new Transformation[
      Endpoint[Op, _, _, _, _, _],
      SmithyPlayClientEndpoint[F, Op, _, _, _, _, _]
    ] {
      def apply[I, E, O, SI, SO](
          endpoint: Endpoint[Op, I, E, O, SI, SO]
      ): SmithyPlayClientEndpoint[F, Op, I, E, O, SI, SO] =
        SmithyPlayClientEndpoint(
          baseUri,
          endpoint,
        ).getOrElse(
          sys.error(
            s"Operation ${endpoint.name} is not bound to http semantics"
          )
        )
    }.precompute(service.endpoints.map(smithy4s.Kind5.existential(_)))
}

*/
